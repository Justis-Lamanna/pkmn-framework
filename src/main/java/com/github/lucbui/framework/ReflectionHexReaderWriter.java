package com.github.lucbui.framework;

import com.github.lucbui.annotations.*;
import com.github.lucbui.bytes.*;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A hex reader and writer that reads and writes values via annotations.
 * Using reflection, an arbitrary object can be read/write via its annotations. An object read in this way *MUST* have
 * an {@code @DataStructure} annotation at the class definition level. Fields can then be specified using the {@code @StructField}
 * annotation. See documentation of both classes for more information.
 * @param <T>
 */
public class ReflectionHexReaderWriter<T> implements HexReader<T>, HexWriter<T> {

    //A cache of readers, registered for reusing.
    static Map<Class<?>, HexReader<?>> READERS = new HashMap<>();
    static Map<Class<?>, HexWriter<?>> WRITERS = new HashMap<>();
    static {
        resetReaders();
        resetWriters();
    }

    /**
     * Reset Readers to the default.
     */
    public static void resetReaders(){
        READERS.clear();
        READERS.put(UnsignedByte.class, UnsignedByte.HEX_READER);
        READERS.put(UnsignedShort.class, UnsignedShort.HEX_READER);
        READERS.put(UnsignedWord.class, UnsignedWord.HEX_READER);
    }

    /**
     * Register a number of readers.
     * @param readers The readers to add.
     */
    public static void addReaders(Map<Class<?>, HexReader<?>> readers){
        READERS.putAll(readers);
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
    private final RepointStrategy repointStrategy;

    private ReflectionHexReaderWriter(Class<T> clazz){
        this.clazz = clazz;
        this.repointStrategy = RepointStrategy.DISABLE_REPOINT;
    }

    private ReflectionHexReaderWriter(Class<T> clazz, RepointStrategy strategy){
        this.clazz = clazz;
        this.repointStrategy = strategy;
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
     * 4. After all fields are written, all methods annotated with {@code @AfterRead} *that are in the created class*
     * are called. Methods should have no parameters, or else an IllegalArgumentException will be thrown. Methods may be of any
     * @param iterator The iterator to read from.
     * @return
     */
    @Override
    public T read(HexFieldIterator iterator) {
        try {
            T object = clazz.newInstance();
            //Fill in fields.
            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, StructField.class);
            for(Field field : fields){
                StructField annotation = field.getAnnotation(StructField.class);
                int offset = annotation.value();
                if(field.getType().equals(PointerObject.class)){
                    //PointerObjects are special cases.
                    Class<? extends Pointer> ptrClass = annotation.pointerType();
                    Class<? extends Object> objClass = annotation.objectType();
                    Pointer ptr = (Pointer) getHexReaderFor(ptrClass).read(iterator);
                    Object obj = getHexReaderFor(objClass).read(iterator.copy(ptr.getLocation()));
                    FieldUtils.writeField(field, object, new PointerObject<>(ptr, obj), true);
                } else {
                    Class<?> classToRead = field.getType();
                    HexReader<?> reader = getHexReaderFor(classToRead);
                    Object parsedObject = reader.read(iterator.copyRelative(offset));
                    FieldUtils.writeField(field, object, parsedObject, true);
                }
            }
            //Invoke all AfterRead methods.
            MethodUtils
                    .getMethodsListWithAnnotation(clazz, AfterRead.class, false, true)
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
                return new ReflectionHexReaderWriter<>(type);
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
        if(ReflectionHexReaderWriter.READERS.containsKey(type)){
            return ReflectionHexReaderWriter.READERS.get(type);
        }
        Map<Class<?>, HexReader<?>> readers = ReflectionHexReaderWriter.READERS.keySet().stream()
                .filter(type::isAssignableFrom)
                .collect(Collectors.toMap(Function.identity(), ReflectionHexReaderWriter.READERS::get));
        if(readers.isEmpty()){
            return null;
        } else if(readers.size() > 1){
            throw new IllegalArgumentException("Error finding reader: " + type + " matches: " + readers.keySet() + ". Please disambiguate the reader.");
        } else {
            return readers.values().stream().findFirst().orElse(null);
        }
    }

    @Override
    public void write(T object, HexFieldIterator iterator) {

        //Invoke all BeforeWrite methods.
        MethodUtils
                .getMethodsListWithAnnotation(clazz, BeforeWrite.class, false, true)
                .forEach(m -> {
                    try {
                        MethodUtils.invokeMethod(object, true, m.getName());
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        throw new IllegalArgumentException("Error invoking method " + m.getName(), e);
                    }
                });

        try {
            //Get all StructFields that aren't read only.
            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, StructField.class).stream()
                    .filter(i -> !i.getAnnotation(StructField.class).readOnly()).collect(Collectors.toList());
            for (Field field : fields) {
                StructField annotation = field.getAnnotation(StructField.class);
                int offset = annotation.value();
                Class<?> classToWrite = field.getType();
                HexWriter<?> writer = getHexWriterFor(classToWrite, repointStrategy);
                writer.writeObject(FieldUtils.readField(field, object, true), iterator.copyRelative(offset));
            }
        } catch (IllegalAccessException ex){
            throw new IllegalArgumentException("Error writing object of type " + clazz.getName(), ex);
        }
    }

    private RepointStrategy.RepointMetadata createRepointMetadata(HexFieldIterator iterator, int offset) {
        return new RepointStrategy.RepointMetadata( 0);
    }

    public static HexWriter<?> getHexWriterFor(Class<?> type, RepointStrategy repointStrategy) {
        HexWriter<?> reader = findHexWriterInSubclasses(type);
        if(reader != null){
            return reader;
        } else {
            if(type.isAnnotationPresent(DataStructure.class)) {
                return new ReflectionHexReaderWriter<>(type, repointStrategy);
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
        if(ReflectionHexReaderWriter.WRITERS.containsKey(type)){
            return ReflectionHexReaderWriter.WRITERS.get(type);
        }
        Map<Class<?>, HexWriter<?>> writers = ReflectionHexReaderWriter.WRITERS.keySet().stream()
                .filter(type::isAssignableFrom)
                .collect(Collectors.toMap(Function.identity(), ReflectionHexReaderWriter.WRITERS::get));
        if(writers.isEmpty()){
            return null;
        } else if(writers.size() > 1){
            throw new IllegalArgumentException("Error finding writer: " + type + " matches: " + writers.keySet() + ". Please disambiguate the writer.");
        } else {
            return writers.values().stream().findFirst().orElse(null);
        }
    }
}
