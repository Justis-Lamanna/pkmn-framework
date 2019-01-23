package com.github.lucbui.pipeline;

import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.utility.HexerUtils;

/**
 * A ReadPipe which correctly handles PointerFields.
 * If the incoming field has the @PointerField annotation, a Pointer is read at the iterator, rather than
 * the normal object. The actual object is then read from that Pointer.
 *
 * If @PointerField is not encountered, the object is read as normal.
 */
public interface PointerFieldFriendlyReadPipe extends ReadPipe<FieldObject> {
    @Override
    default void read(FieldObject object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        if (object.isAnnotationPresent(PointerField.class)) {
            HexFieldIterator iteratorForField = iterator.copy(object.getPointer().getLocation());
            Pointer pointer = HexerUtils.getHexerFor(pkmnFramework.getHexers(), Pointer.class)
                    .map(hexer -> hexer.read(iteratorForField))
                    .orElseThrow(() -> new ReadPipeException("Attempted to read PointerField without Pointer hexer registered"));
            iteratorForField.advanceTo(pointer.getLocation());

            Object internalObject = makeObject(object, iteratorForField, pkmnFramework);

            object.setReferent(new PointerObject<>(pointer, internalObject));
        } else {
            Object internalObject = makeObject(object, iterator, pkmnFramework);
            object.setReferent(internalObject);
        }
    }

    /**
     * Create the object that should be wrapped in the PointerObject, if it is a PointerField.
     * @param object The FieldObject being used
     * @param iterator The iterator to use
     * @param pkmnFramework The framework being used
     * @return The created object
     */
    Object makeObject(FieldObject object, HexFieldIterator iterator, PkmnFramework pkmnFramework);
}
