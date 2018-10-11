package com.github.lucbui.framework;

import com.github.lucbui.file.Pointer;

import java.util.Optional;
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
     * Determine where and how to repoint data, if it needs to be repointed.
     * @param metadata Object describing the state of the object to repoint.
     * @return The position to repoint to.
     */
    Pointer repoint(RepointMetadata metadata);

    /**
     * An object describing the state needed for repointing.
     */
    class RepointMetadata{
        private int size;

        RepointMetadata(int size){
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
    }
}
