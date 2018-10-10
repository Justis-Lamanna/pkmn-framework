package com.github.lucbui.framework;

import com.github.lucbui.file.Pointer;

import java.util.OptionalInt;

/**
 * A strategy that describes how to repoint data in case of write.
 */
public interface RepointStrategy {
    long repoint(RepointMetadata metadata);

    /**
     * An object describing the state needed for repointing.
     */
    class RepointMetadata{
        private Pointer oldPointer;
        private int size;

        RepointMetadata(Pointer oldPointer, int size){
            this.oldPointer = oldPointer;
            this.size = size;
        }

        /**
         * Get the size of the data to be repointed.
         * If the size is indeterminate, an empty OptionalInt is returned.
         * @return An OptionalInt that is empty if the size is indeterminate, or contains the size of the data to repoint.
         */
        public OptionalInt getSize(){
            return this.size <= 0 ? OptionalInt.empty() : OptionalInt.of(this.size);
        }

        public Pointer getOldPointer(){
            return oldPointer;
        }
    }
}
