package com.github.lucbui.framework;

import com.github.lucbui.annotations.*;
import com.github.lucbui.bytes.*;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
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
public class ReflectionHexReaderWriter<T> implements Hexer<T> {

    public static final List<Class<? extends Annotation>> STRUCT_FIELD_MARKER_ANNOTATION_CLASSES = Arrays.asList(Offset.class);

    //A cache of readers, registered for reusing.
    static Map<Class<?>, Hexer<?>> HEXERS = new HashMap<>();
    static {
        resetHexers();
    }

    /**
     * Reset Readers to the default.
     */
    public static void resetHexers(){
        HEXERS.clear();
        HEXERS.put(UnsignedByte.class, UnsignedByte.HEXER);
        HEXERS.put(UnsignedWord.class, UnsignedWord.HEXER);
        HEXERS.put(UnsignedShort.class, UnsignedShort.HEXER);
    }

    /**
     * Register a number of readers.
     * @param readers The readers to add.
     */
    public static void addHexer(Map<Class<?>, Hexer<?>> readers){
        HEXERS.putAll(readers);
    }

    private final Class<T> clazz;
    private final Evaluator evaluator;

    private ReflectionHexReaderWriter(Class<T> clazz, Evaluator evaluator){
        this.clazz = clazz;
        this.evaluator = evaluator;
    }

    private List<Field> getStructFields(Class<?> clazz){
        return FieldUtils.getAllFieldsList(clazz).stream()
                .filter(i -> STRUCT_FIELD_MARKER_ANNOTATION_CLASSES.stream().anyMatch(i::isAnnotationPresent))
                .collect(Collectors.toList());
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

        long startPosition = iterator.getPosition();

        try {
            T object = invokeConstructor(clazz);
            //Fill in fields.
            List<Field> fields = getStructFields(clazz);
            for(Field field : fields){
                Offset offsetAnnotation = field.getAnnotation(Offset.class);
                long offset = evaluator.evaluateLong(offsetAnnotation.value()).orElseThrow(RuntimeException::new);

                Class<?> classToRead = field.getType(); //The class of this field. If it's a pointer object, this becomes the objectField.

                HexFieldIterator fieldIterator;
                if(field.isAnnotationPresent(Absolute.class)){
                    fieldIterator = iterator.copy(offset);
                } else {
                    fieldIterator = iterator.copyRelative(offset); //An iterator starting at offset. If it's a pointer object, this moves to the pointed value.
                }

                if(field.isAnnotationPresent(PointerField.class) && field.getType().equals(PointerObject.class)){
                    //Special support for Pointer objects, which store a pointer to themselves, as well as data poined to.
                    PointerField pointerAnnotation = field.getAnnotation(PointerField.class);
                    classToRead = pointerAnnotation.objectType();
                    Pointer ptr = (Pointer) getHexReaderFor(pointerAnnotation.pointerType()).read(fieldIterator);
                    Object parsedObject = getHexReaderFor(classToRead).read(iterator.copy(ptr.getLocation())); //The object read.

                    FieldUtils.writeDeclaredField(object, field.getName(), new PointerObject<>(ptr, parsedObject), true);
                    fieldIterator.advanceTo(ptr.getLocation());
                } else {
                    //No special modifications necessary.
                    Object parsedObject = getHexReaderFor(classToRead).read(fieldIterator);

                    FieldUtils.writeDeclaredField(object, field.getName(), parsedObject, true);
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

    private T invokeConstructor(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        return clazz.newInstance();
    }

    public static Hexer<?> getHexerFor(Class<?> type, Evaluator evaluator) {
        Hexer<?> reader = findHexerInSubclasses(type);
        if(reader != null){
            return reader;
        } else {
            if(type.isAnnotationPresent(DataStructure.class)) {
                return new ReflectionHexReaderWriter<>(type, evaluator);
            } else {
                throw new IllegalArgumentException("Requested type " + type.getName() + " does not contain @DataStructure or an associated reader. Use PkmnFramework.addReader() to add a class-reader association.");
            }
        }
    }

    private Hexer<?> getHexReaderFor(Class<?> type) {
        return getHexerFor(type, evaluator);
    }

    /**
     * Tries to find the best matching HexReader for the provided type.
     * If the direct type provided is not associated to a reader, each reader is scanned. If
     * an appropriate reader matches a subclass of the specified type, it is used instead.
     * @param type
     * @return
     */
    private static Hexer<?> findHexerInSubclasses(Class<?> type) {
        if(ReflectionHexReaderWriter.HEXERS.containsKey(type)){
            return ReflectionHexReaderWriter.HEXERS.get(type);
        }
        Map<Class<?>, Hexer<?>> readers = ReflectionHexReaderWriter.HEXERS.keySet().stream()
                .filter(type::isAssignableFrom)
                .collect(Collectors.toMap(Function.identity(), ReflectionHexReaderWriter.HEXERS::get));
        if(readers.isEmpty()){
            return null;
        } else if(readers.size() > 1){
            throw new IllegalArgumentException("Error finding reader: " + type + " matches: " + readers.keySet() + ". Please disambiguate the reader.");
        } else {
            return readers.values().stream().findFirst().orElseThrow(RuntimeException::new);
        }
    }

    @Override
    public void write(T object, HexFieldIterator iterator) {

        long startPosition = iterator.getPosition();

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
            List<Field> fields = getStructFields(clazz);
            ByteWindow bw = new ByteWindow();
            HexFieldIterator fieldIterator = bw.iterator();
            for (Field field : fields) {

                Offset offsetAnnotation = field.getAnnotation(Offset.class);
                long offset = evaluator.evaluateLong(offsetAnnotation.value()).orElseThrow(RuntimeException::new);

                if(field.isAnnotationPresent(Absolute.class)){
                    fieldIterator.advanceTo(offset);
                } else {
                    fieldIterator.advanceTo(startPosition + offset);
                }

                Object writingObject = FieldUtils.readDeclaredField(object, field.getName(), true); //The object we are writing.
                Class<?> classToWrite = field.getType();

                if(field.isAnnotationPresent(PointerField.class) && field.getType().equals(PointerObject.class)){
                    PointerField ptrAnnotation = field.getAnnotation(PointerField.class);
                    RepointStrategy repointStrategy;
                    try {
                        repointStrategy = ptrAnnotation.repointStrategy().newInstance();
                    } catch (InstantiationException e) {
                        throw new RuntimeException("Error instantiating repoint strategy. Does your RepointStrategy have an empty constructor?");
                    }
                    PointerObject<?> po = (PointerObject<?>) writingObject;
                    Pointer ptr = repointStrategy.repoint(new RepointStrategy.RepointMetadata(po, getSize(ptrAnnotation.objectType(), po.getObject())));
                    getHexerFor(ptrAnnotation.pointerType(), evaluator).writeObject(ptr, fieldIterator);
                    fieldIterator.advanceTo(ptr.getLocation());
                    writingObject = ((PointerObject<?>) writingObject).getObject();
                    classToWrite = ptrAnnotation.objectType();

                    getHexerFor(classToWrite, evaluator).writeObject(writingObject, fieldIterator);
                } else {
                    getHexerFor(classToWrite, evaluator).writeObject(writingObject, fieldIterator);
                }
            }
            iterator.write(bw);
        } catch (IllegalAccessException ex){
            throw new IllegalArgumentException("Error writing object of type " + clazz.getName(), ex);
        }
    }

    /**
    * Get the size of the object.
     * This is calculated by adding the size of every field in the structure together, recursively if necessary.
    * @param objectClass The class of the object.
    * @param object The object itself.
    * @return
    */
    private int getSize(Class<?> objectClass, Object object) {
        Map<Boolean, List<OptionalInt>> sizes = getStructFields(objectClass).stream()
                .map(field ->
                {
                    try {
                        int size = getHexerFor(field.getType(), evaluator)
                                .getSizeAsObject(FieldUtils.readDeclaredField(object, field.getName(), true));
                        return OptionalInt.of(size);
                    } catch (Exception ex) {
                        return OptionalInt.empty();
                    }
                })
                .collect(Collectors.partitioningBy(OptionalInt::isPresent));
        if(sizes.get(false).isEmpty()){
            return sizes.get(true).stream().mapToInt(OptionalInt::getAsInt).sum();
        } else {
            return -1;
        }
    }

    @Override
    public int getSize(T object) {
        return getSize(object.getClass(), object);
    }
}
