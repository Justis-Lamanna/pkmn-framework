package com.github.lucbui.framework;

import com.github.lucbui.file.Pointer;

import java.util.OptionalInt;

/**
 * A strategy that describes how to repoint data in case of write.
 */
public interface RepointStrategy {

    /**
     * A repoint strategy where no repointing should occur.
     * If a repoint attempts to occur with this strategy, an IllegalStateException is thrown.
     */
    public static final RepointStrategy DISABLE_REPOINT = metadata -> {
        throw new IllegalStateException("Cannot repoint.");
    };

    /**
     * A repoint strategy where the data is written to the same place as it was.
     * If the data pointed to stays the same size, no repoint logic may be necessary, and it can simply be
     * written to the same place it was read.
     */
    public static final RepointStrategy IDENTITY_REPOINT = metadata -> metadata.getOldPointer().getLocation();

    /**
     * Determine where and how to repoint data, if it needs to be repointed.
     * @param metadata Object describing the state of the object to repoint.
     * @return The position to repoint to.
     */
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
