package com.github.lucbui.framework;

import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.file.Pointer;

import java.util.OptionalInt;

/**
 * A strategy that describes how to repoint data in case of write.
 */
public interface RepointStrategy {

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
        private PointerObject<? extends Pointer, ?> pointerObject;
        private int size;

        RepointMetadata(PointerObject<? extends Pointer, ?> pointerObject, int size){
            this.size = size;
            this.pointerObject = pointerObject;
        }

        /**
         * Get the size of the data to be repointed.
         * If the size is indeterminate, an empty OptionalInt is returned.
         * @return An OptionalInt that is empty if the size is indeterminate, or contains the size of the data to repoint.
         */
        public OptionalInt getSize(){
            return this.size <= 0 ? OptionalInt.empty() : OptionalInt.of(this.size);
        }

        public PointerObject<? extends Pointer, ?> getPointerObject() {
            return pointerObject;
        }
    }
}
