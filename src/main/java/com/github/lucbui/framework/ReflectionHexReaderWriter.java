package com.github.lucbui.framework;

import com.github.lucbui.annotations.*;
import com.github.lucbui.bytes.*;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 *
 * If a write fails, the structure will *not* be written to disk.
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
        this.repointStrategy = RepointUtils.disableRepointStrategy();
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
            T object = invokeConstructor(clazz);
            //Fill in fields.
            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, StructField.class);
            for(Field field : fields){
                StructField structAnnotation = field.getAnnotation(StructField.class);
                int offset = structAnnotation.offset();
                //Special support for Pointer objects, which store a pointer to themselves, as well as data poined to.
                Pointer ptr = null; //If this a PointerField, ptr contains the Pointer. Otherwise it's null.
                Object parsedObject = null; //The object read. If it's a pointer object, it's the object pointed to.
                Class<?> classToRead = field.getType(); //The class of this field. If it's a pointer object, this becomes the objectField.
                HexFieldIterator fieldIterator = iterator.copyRelative(offset); //An iterator starting at offset. If it's a pointer object, this moves to the pointed value.
                if(field.isAnnotationPresent(PointerField.class) && field.getType().equals(PointerObject.class)){
                    PointerField pointerAnnotation = field.getAnnotation(PointerField.class);
                    classToRead = pointerAnnotation.objectType();
                    ptr = (Pointer) getHexReaderFor(pointerAnnotation.pointerType()).read(iterator);
                    fieldIterator.advanceTo(ptr.getLocation());
                }

                parsedObject = getHexReaderFor(classToRead).read(fieldIterator);

                //Wrap up the object in a PointerObject if necessary.
                FieldUtils.writeDeclaredField(object, field.getName(), ptr == null ? parsedObject : new PointerObject<>(ptr, parsedObject), true);
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

    private T invokeConstructor(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
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
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error invoking method " + m.getName(), e);
                    }
                });

        try {
            //Get all StructFields that aren't read only.
            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, StructField.class).stream()
                    .filter(i -> !i.getAnnotation(StructField.class).readOnly()).collect(Collectors.toList());
            ByteWindow bw = new ByteWindow();
            HexFieldIterator fieldIterator = bw.iterator();
            for (Field field : fields) {
                StructField structAnnotation = field.getAnnotation(StructField.class);
                int offset = structAnnotation.offset();

                fieldIterator.advanceTo(offset); //The iterator we're writing with.
                Object writingObject = FieldUtils.readDeclaredField(object, field.getName(), true); //The object we are writing.
                Class<?> classToWrite = field.getType();

                if(field.isAnnotationPresent(PointerField.class) && field.getType().equals(PointerObject.class)){
                    PointerField ptrAnnotation = field.getAnnotation(PointerField.class);
                    PointerObject<? extends Pointer, ?> po = (PointerObject<? extends Pointer, ?>) writingObject;
                    Pointer ptr = repointStrategy.repoint(new RepointStrategy.RepointMetadata(po, getSize(ptrAnnotation.objectType(), po.getObject())));
                    getHexWriterFor(ptrAnnotation.pointerType(), repointStrategy).writeObject(ptr, fieldIterator);
                    fieldIterator.advanceTo(ptr.getLocation());
                    writingObject = ((PointerObject<? extends Pointer, ?>) writingObject).getObject();
                    classToWrite = ptrAnnotation.objectType();
                }
                getHexWriterFor(classToWrite, repointStrategy).writeObject(writingObject, fieldIterator);
            }
            iterator.writeRelative(iterator.getPosition(), bw);
        } catch (IllegalAccessException ex){
            throw new IllegalArgumentException("Error writing object of type " + clazz.getName(), ex);
        }
    }

        /**
        * Get the size of the object attempting to repoint.
        * If the size is specified in teh DataStructure object, we return with it.
        * Variable sizes can be provided by annotating a method with @DataStructureSize in the object to observe, which
        * should take no parameters and return an integer.
        * @param objectClass The class of the object.
        * @param object The object itself.
        * @return
        */
        private int getSize(Class<?> objectClass, Object object) {
            if(objectClass.isAnnotationPresent(DataStructure.class)){
                DataStructure ds = objectClass.getAnnotation(DataStructure.class);
                if(ds.size() > 0){
                    return ds.size();
                }
            }
            List<Method> methods = MethodUtils.getMethodsListWithAnnotation(objectClass, DataStructureSize.class);
            if(methods.size() == 1){
                try{
                    return (int)MethodUtils.invokeMethod(object, true, methods.get(0).getName());
                } catch (Exception e) {
                    throw new IllegalArgumentException("Error invoking method " + methods.get(0).getName(), e);
                }
            } else if(methods.size() > 1){
                throw new IllegalArgumentException("Multiple methods with @DataStructureSize annotation specified. Please ensure there is only one.");
            }
            return -1;
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


//    /**
//     * Annotation Function for the PointerField.
//     * This is stored here because we need readers and writers.
//     */
//    private static class PointerObjectAnnotationFunction implements ReflectionAnnotationFunction{
//
//        @Override
//        public void onRead(Object object, Field field, HexFieldIterator iterator) {
//            try {
//                PointerField pointerAnnotation = field.getAnnotation(PointerField.class);
//                Class<? extends Pointer> ptrClass = pointerAnnotation.pointerType();
//                Class<?> objClass = pointerAnnotation.objectType();
//                Pointer ptr = (Pointer) getHexReaderFor(ptrClass).read(iterator);
//                Object ptrObj = getHexReaderFor(objClass).read(iterator.copy(ptr.getLocation()));
//                FieldUtils.writeField(field, object, new PointerObject<>(ptr, ptrObj), true);
//            } catch (IllegalAccessException e) {
//                throw new IllegalStateException(e);
//            }
//        }
//
//        @Override
//        public void onWrite(Object object, Field field, HexFieldIterator iterator, RepointStrategy repointStrategy) {
//            try{
//                int offset = field.getAnnotation(StructField.class).offset();
//                PointerField pointerAnnotation = field.getAnnotation(PointerField.class);
//                PointerObject<? extends Pointer, ?> pointerObj = (PointerObject<? extends Pointer, ?>) FieldUtils.readField(field, object, true);
//                Pointer repoint = repointStrategy.repoint(createRepointMetadata(pointerObj, getSize(pointerAnnotation.objectType(), pointerObj.getObject())));
//                HexWriter<?> ptrWriter = getHexWriterFor(pointerAnnotation.pointerType(), repointStrategy);
//                HexWriter<?> objWriter = getHexWriterFor(pointerAnnotation.objectType(), repointStrategy);
//                ptrWriter.writeObject(repoint, iterator.copyRelative(offset));
//                objWriter.writeObject(pointerObj.getObject(), iterator.copy(repoint.getLocation()));
//            } catch (IllegalAccessException e) {
//                throw new IllegalStateException(e);
//            }
//        }
//
//        private RepointStrategy.RepointMetadata createRepointMetadata(PointerObject<? extends Pointer, ?> pointerObject, int size) {
//            return new RepointStrategy.RepointMetadata(pointerObject, size);
//        }
//
//        /**
//         * Get the size of the object attempting to repoint.
//         * If the size is specified in teh DataStructure object, we return with it.
//         * Variable sizes can be provided by annotating a method with @DataStructureSize in the object to observe, which
//         * should take no parameters and return an integer.
//         * @param objectClass The class of the object.
//         * @param object The object itself.
//         * @return
//         */
//        private int getSize(Class<?> objectClass, Object object) {
//            if(objectClass.isAnnotationPresent(DataStructure.class)){
//                DataStructure ds = objectClass.getAnnotation(DataStructure.class);
//                if(ds.size() > 0){
//                    return ds.size();
//                }
//            }
//            List<Method> methods = MethodUtils.getMethodsListWithAnnotation(objectClass, DataStructureSize.class);
//            if(methods.size() == 1){
//                try{
//                    return (int)MethodUtils.invokeMethod(object, true, methods.get(0).getName());
//                } catch (Exception e) {
//                    throw new IllegalArgumentException("Error invoking method " + methods.get(0).getName(), e);
//                }
//            } else if(methods.size() > 1){
//                throw new IllegalArgumentException("Multiple methods with @DataStructureSize annotation specified. Please ensure there is only one.");
//            }
//            return -1;
//        }
//    }
}
