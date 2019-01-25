package com.github.lucbui.utility;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.DataStructureSize;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.framework.HexFramework;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HexerUtils {
    /**
     * Search a map of hexers for an appropriate one for a given class
     * @param hexers The hexers to search
     * @param type The type to search for
     * @param <T> The type to search for
     * @return An Optional containing a Hexer, if found, or an empty Optional if none were found
     */
    public static <T> Optional<Hexer<T>> getHexerFor(Map<Class<?>, Hexer<?>> hexers, Class<T> type) {
        Hexer<T> reader = findHexerInSubclasses(hexers, type);
        return Optional.ofNullable(reader);
    }

    @SuppressWarnings("unchecked")
    private static <T> Hexer<T> findHexerInSubclasses(Map<Class<?>, Hexer<?>> hexers, Class<T> type) {
        if(hexers.containsKey(type)){
            return (Hexer<T>) hexers.get(type);
        }
        Map<Class<?>, Hexer<?>> readers = hexers.keySet().stream()
                .filter(type::isAssignableFrom)
                .collect(Collectors.toMap(Function.identity(), hexers::get));
        if(readers.isEmpty()){
            return null;
        } else if(readers.size() > 1){
            throw new IllegalArgumentException("Error finding reader: " + type + " matches: " + readers.keySet() + ". Please disambiguate the reader.");
        } else {
            return (Hexer<T>) readers.values().stream().findFirst().orElseThrow(RuntimeException::new);
        }
    }

    /**
     * Calculate the size of the object as best as physically possible.
     * In order, the following will be evaluated:
     * 1. Try to find an appropriate hexer. If found, and a positive size is returned from the getSize(), this size is returned
     * 2. Try to find a size in the class's @DataStructure field. If found and it is positive, this size is returned
     * 3. Try to find a method in the object annotated by @DataStructureSize. If one is found, it is invoked, and if it is positive, this size is returned
     * 4. Try to calculate the size, by summing up the size of each field, found by recursively calling this method on each field.
     * If all methods fail, an exception is thrown.
     * @param hexFramework The framework being used.
     * @param obj The object being used.
     * @return An OptionalInt describing the object's size, or empty if the size is indeterminate
     */
    public static OptionalInt calculateSizeOfObject(HexFramework hexFramework, Object obj){
        Class<?> clazz = obj.getClass();

        OptionalInt result = getSizeFromHexer(hexFramework.getHexers(), obj);
        if(result.isPresent()){
            return result;
        }
        result = getSizeFromDataStructureAnnotation(clazz);
        if(result.isPresent()){
            return result;
        }
        result = getSizeFromDataStructureSizeAnnotation(obj);
        if(result.isPresent()){
            return result;
        }
        return getSizeFromFields(hexFramework, obj);
    }

    private static OptionalInt getSizeFromHexer(Map<Class<?>, Hexer<?>> hexers, Object obj){
        return getHexerFor(hexers, obj.getClass()) //Get the hexer
                .map(hexer -> hexer.getSizeAsObject(obj)) //If it exists, calculate the size
                .filter(size -> size > 0) //Nonpositive size means "Cannot calculate"
                .map(OptionalInt::of) //Turn into an OptionalInt
                .orElse(OptionalInt.empty()); //If the hexer does not exist, or the size is negative, return empty OptionalInt

    }

    private static OptionalInt getSizeFromDataStructureAnnotation(Class<?> clazz){
        if(clazz.isAnnotationPresent(DataStructure.class)){
            int size = clazz.getAnnotation(DataStructure.class).size();
            if(size > 0){
                return OptionalInt.of(size);
            }
        }
        return OptionalInt.empty();
    }

    private static OptionalInt getSizeFromDataStructureSizeAnnotation(Object obj){
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(obj.getClass(), DataStructureSize.class);
        if(methods.isEmpty()){
            return OptionalInt.empty();
        } else if(methods.size() > 1){
            throw new IllegalArgumentException("Multiple methods annotated with @DataStructureSize. Only one may be annotated");
        } else {
            try {
                int size = (int) MethodUtils.invokeMethod(obj, true, methods.get(0).getName());
                return size > 0 ? OptionalInt.of(size) : OptionalInt.empty();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Error invoking method " + methods.get(0).getName(), e);
            }
        }
    }

    private static OptionalInt getSizeFromFields(HexFramework hexFramework, Object obj){
        if(!obj.getClass().isAnnotationPresent(DataStructure.class)){
            //Class not marked DataStructure cannot be properly sized.
            return OptionalInt.empty();
        }
        //The size of the object is equal to the maximum offset of the object, plus the size of the end byte, minus one.
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(obj.getClass(), Offset.class);
        if(fields.isEmpty()){
            //No annotated fields means there is no size.
            return OptionalInt.of(0);
        }
        boolean hasAbsolute = fields.stream().anyMatch(f -> f.isAnnotationPresent(Absolute.class));
        if(hasAbsolute){
            //We cannot calculate the size of an object with absolute
            return OptionalInt.empty();
        }
        //TODO: Logic to calculate the actual size.
        return OptionalInt.empty();
    }

}
