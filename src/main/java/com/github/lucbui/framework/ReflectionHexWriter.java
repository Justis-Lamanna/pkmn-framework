package com.github.lucbui.framework;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.StructField;
import com.github.lucbui.annotations.StructFieldType;
import com.github.lucbui.bytes.*;
import com.github.lucbui.file.HexFieldIterator;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReflectionHexWriter<T> implements HexWriter<T> {

    //A cache of writers, registered for reusing.
    static Map<Class<?>, HexWriter<?>> WRITERS = new HashMap<>();
    static {
        resetWriters();
    }

    /**
     * Reset Writers to the default.
     */
    public static void resetWriters(){
        WRITERS.clear();
        WRITERS.put(UnsignedByte.class, UnsignedByte.HEX_WRITER);
        WRITERS.put(UnsignedShort.class, UnsignedShort.HEX_WRITER);
        WRITERS.put(UnsignedWord.class, UnsignedWord.HEX_WRITER);
    }

    /**
     * Register a number of writers.
     * @param writers The writers to add.
     */
    public static void addWriters(Map<Class<?>, HexWriter<?>> writers){
        WRITERS.putAll(writers);
    }

    private final Class<T> clazz;

    public ReflectionHexWriter(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void write(Object object, HexFieldIterator iterator) {
        try {
            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, StructField.class);
            for (Field field : fields) {
                StructField annotation = field.getAnnotation(StructField.class);
                int offset = annotation.value();
                if (annotation.fieldType() == StructFieldType.NESTED) {
                    Class<?> classToWrite = annotation.readAs() == Void.class ? field.getType() : annotation.readAs();
                    HexWriter<?> writer = getHexWriterFor(classToWrite);
                    writer.writeObject(FieldUtils.readField(field, object, true), iterator.copyRelative(offset));
                } else if (annotation.fieldType() == StructFieldType.POINTER) {
                    throw new NotImplementedException("Cannot supported Pointer writing yet");
                } else {
                    throw new IllegalArgumentException("Illegal fieldType read: " + annotation.fieldType());
                }
            }
        } catch (IllegalAccessException ex){
            throw new IllegalArgumentException("Error writing object of type " + clazz.getName(), ex);
        }
    }

    public static HexWriter<?> getHexWriterFor(Class<?> type) {
        HexWriter<?> reader = findHexWriterInSubclasses(type);
        if(reader != null){
            return reader;
        } else {
            if(type.isAnnotationPresent(DataStructure.class)) {
                return new ReflectionHexWriter<>(type);
            } else {
                throw new IllegalArgumentException("Requested type " + type.getName() + " does not contain @DataStructure or an associated writer. Use PkmnFramework.addWriter() to add a class-writer association.");
            }
        }
    }

    /**
     * Tries to find the best matching HexWriter for the provided type.
     * If the direct type provided is not associated to a writer, each registered writer is scanned. If
     * an appropriate writer matches a subclass of the specified type, it is used instead.
     * @param type
     * @return
     */
    private static HexWriter<?> findHexWriterInSubclasses(Class<?> type) {
        if(ReflectionHexWriter.WRITERS.containsKey(type)){
            return ReflectionHexWriter.WRITERS.get(type);
        }
        Map<Class<?>, HexWriter<?>> writers = ReflectionHexWriter.WRITERS.keySet().stream()
                .filter(type::isAssignableFrom)
                .collect(Collectors.toMap(Function.identity(), ReflectionHexWriter.WRITERS::get));
        if(writers.isEmpty()){
            return null;
        } else if(writers.size() > 1){
            throw new IllegalArgumentException("Error finding writer: " + type + " matches: " + writers.keySet() + ". Please disambiguate the writer.");
        } else {
            return writers.values().stream().findFirst().orElse(null);
        }
    }
}
