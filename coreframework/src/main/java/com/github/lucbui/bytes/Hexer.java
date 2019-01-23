package com.github.lucbui.bytes;

public interface Hexer<T> extends HexReader<T>, HexWriter<T>{
    /**
     * Get the size of an object, in bytes.
     * @param object The object to analyze.
     * @return The number of bytes
     */
    int getSize(T object);

    /**
     * Get the size of an object, coercing into T
     * @param obj
     * @return
     */
    default int getSizeAsObject(Object obj){
        return getSize((T)obj);
    }
}
