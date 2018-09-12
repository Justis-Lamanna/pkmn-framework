package com.github.lucbui.annotations;

/**
 * An enum which further describes the type of field
 */
public enum StructFieldType {
    /**
     * With a Nested StructField, the object to read is at the specified offset.
     */
    NESTED,
    /**
     * With a Pointer StructField, the object to read is referenced, via a pointer at the specified offset.
     * For instance, if a StructField is defined to begin at offset 0, and has a Pointer StructFieldType, a pointer
     * is read at offset 0, and the contained object is parsed beginning at the read pointer.
     */
    POINTER
}
