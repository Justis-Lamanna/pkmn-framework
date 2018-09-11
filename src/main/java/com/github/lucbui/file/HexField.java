package com.github.lucbui.file;

/**
 * An abstraction of a HexField
 * A hex file is some sort of hex field that can be traversed in a randomly-accessed format.
 */
public interface HexField {

    /**
     * Get an iterator to traverse this hex field.
     * @param position The position to start the iterator at
     * @return A HexFieldIterator to iterate over.
     */
    HexFieldIterator iterator(long position);

    /**
     * Get an iterator to traverse this hex field.
     * @return A HexFieldIterator to iterate over.
     */
    default HexFieldIterator iterator(){
        return iterator(0);
    }
}
