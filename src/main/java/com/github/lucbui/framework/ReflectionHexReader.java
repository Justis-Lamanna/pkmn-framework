package com.github.lucbui.framework;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.StructField;
import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.bytes.UnsignedShort;
import com.github.lucbui.bytes.UnsignedWord;
import com.github.lucbui.file.GBAPointer;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.PkmnFramework;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Override
    public T translate(HexFieldIterator iterator) {
        try {
            T object = clazz.newInstance();
            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, StructField.class);
            for(Field field : fields){
                StructField annotation = field.getAnnotation(StructField.class);
                int offset = annotation.value();
                HexReader<?> reader = getHexReaderFor(field.getType());
                Object obj = iterator.get(offset, reader);
                field.set(object, obj);
            }
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
