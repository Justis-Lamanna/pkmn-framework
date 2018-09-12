package com.github.lucbui.framework;

import com.github.lucbui.annotations.AfterConstruct;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.StructField;
import com.github.lucbui.annotations.StructFieldType;
import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.bytes.UnsignedShort;
import com.github.lucbui.bytes.UnsignedWord;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A hex reader that reads values via annotations.
 * Using reflection, an arbitrary object can be read via its annotations. An object read in this way *MUST* have
 * an {@code @DataStructure} annotation at the class definition level. Fields can then be specified using the {@code @StructField}
 * annotation. See documentation of both classes for more information.
 * @param <T>
 */
public class ReflectionHexReader<T> implements HexReader<T> {

    //A cache of readers, registered for reusing.
    static Map<Class<?>, HexReader<?>> READERS = new HashMap<>();
    static {
        READERS.put(UnsignedByte.class, UnsignedByte.HEX_READER);
        READERS.put(UnsignedShort.class, UnsignedShort.HEX_READER);
        READERS.put(UnsignedWord.class, UnsignedWord.HEX_READER);
    }

    private final Class<T> clazz;

    private ReflectionHexReader(Class<T> clazz){
        this.clazz = clazz;
    }

    /**
     * Reflexively and recursively build an object from its annotations.
     * Processing occurs like so:
     * 1. An instance of the class is created by invoking the empty constructor. If the class does not have one,
     * an IllegalArgumentException is thrown.
     * 2. All fields marked with a StructField annotation are retrieved, in this class as well as its superclasses. Fields
     * may be public, package-protected, or private. Note that this code deliberately sets a Field's accessibility to true
     * for writing, before setting is back to whatever it was.
     * 3. For each field, the best HexReader is found, by:
     * a. If the StructField contains a runAs parameter, use that.
     * b. Otherwise, use the class of the field itself.
     * c. If a reader is not registered that directly matches the provided class, it searches through
     * the readers that are registered. If there is one, and only one, that is a subclass of the provided type,
     * that one is selected. If there are none, or more than one, an IllegalArgumentException is thrown.
     * 4. After all fields are written, all methods annotated with {@code @AfterConstruct} *that are in the created class*
     * are called. Methods should have no parameters, or else an IllegalArgumentException will be thrown. Methods may be of any
     * @param iterator The iterator to translate from.
     * @return
     */
    @Override
    public T translate(HexFieldIterator iterator) {
        try {
            T object = clazz.newInstance();
            //Fill in fields.
            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, StructField.class);
            for(Field field : fields){
                StructField annotation = field.getAnnotation(StructField.class);
                int offset = annotation.value();
                if(annotation.fieldType() == StructFieldType.NESTED) {
                    Class<?> classToRead = annotation.readAs() == Void.class ? field.getType() : annotation.readAs();
                    HexReader<?> reader = getHexReaderFor(classToRead);
                    Object parsedObject = iterator.get(offset, reader);
                    FieldUtils.writeField(field, object, parsedObject, true);
                } else if(annotation.fieldType() == StructFieldType.POINTER) {
                    HexReader<?> ptrReader = getHexReaderFor(Pointer.class);
                    Pointer ptr = (Pointer)iterator.get(offset, ptrReader);
                    Class<?> classToRead = annotation.readAs() == Void.class ? field.getType() : annotation.readAs();
                    HexReader<?> reader = getHexReaderFor(classToRead);
                    Object parsedObject = iterator.getAbsolute(ptr.getLocation(), reader);
                    FieldUtils.writeField(field, object, parsedObject, true);
                } else {
                    throw new IllegalArgumentException("Illegal fieldType read: " + annotation.fieldType());
                }
            }
            //Invoke all AfterConstruct methods.
            MethodUtils
                    .getMethodsListWithAnnotation(clazz, AfterConstruct.class, false, true)
                    .forEach(m -> {
                        try {
                            MethodUtils.invokeMethod(object, true, m.getName());
                        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                            throw new IllegalArgumentException("Error invoking method " + m.getName(), e);
                        }
                    });
            return object;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalArgumentException("Error building object of type " + clazz.getName(), e);
        }
    }

    public static HexReader<?> getHexReaderFor(Class<?> type) {
        HexReader<?> reader = findHexReaderInSubclasses(type);
        if(reader != null){
            return reader;
        } else {
            if(type.isAnnotationPresent(DataStructure.class)) {
                return new ReflectionHexReader<>(type);
            } else {
                throw new IllegalArgumentException("Requested type " + type.getName() + " does not contain @DataStructure or an associated reader. Use PkmnFramework.addReader() to add a class-reader association.");
            }
        }
    }

    /**
     * Tries to find the best matching HexReader for the provided type.
     * If the direct type provided is not associated to a reader, each reader is scanned. If
     * an appropriate reader matches a subclass of the specified type, it is used instead.
     * @param type
     * @return
     */
    private static HexReader<?> findHexReaderInSubclasses(Class<?> type) {
        if(ReflectionHexReader.READERS.containsKey(type)){
            return ReflectionHexReader.READERS.get(type);
        }
        Map<Class<?>, HexReader<?>> readers = ReflectionHexReader.READERS.keySet().stream().filter(type::isAssignableFrom).collect(Collectors.toMap(Function.identity(), ReflectionHexReader.READERS::get));
        if(readers.isEmpty()){
            return null;
        } else if(readers.size() > 1){
            throw new IllegalArgumentException("Error finding reader: " + type + " matches: " + readers.keySet() + ". Please disambiguate the reader.");
        } else {
            return readers.values().stream().findFirst().orElse(null);
        }
    }
}
