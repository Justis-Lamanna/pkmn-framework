package com.github.lucbui.pipeline;

import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.bytes.RepointMetadata;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.HexFramework;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.utility.HexerUtils;

/**
 * A WritePipe which correctly handles PointerFields
 * If the incoming field has the @PointerField annotation, the repoint strategy associated with the PointerObject
 * is run. The associated pointer is written, the FieldObject's referent is changed to be the PointerObject's object,
 * and the FieldObject's pointer is changed to the new pointer.
 */
public interface PointerFieldFriendlyWritePipe extends WritePipe<FieldObject> {
    @Override
    default void write(HexFieldIterator iterator, FieldObject object, HexFramework hexFramework){
        if(object.isAnnotationPresent(PointerField.class) && object.getReferent() instanceof PointerObject){
            PointerObject po = (PointerObject) object.getReferent();
            Pointer repoint = po.getRepointStrategy().repoint(new RepointMetadata(po));

            HexFieldIterator iteratorForField = iterator.copy(object.getPointer().getLocation());
            Hexer<Pointer> pointHexer = HexerUtils.getHexerFor(hexFramework.getHexers(), Pointer.class)
                    .orElseThrow(ReadPipeException::new);
            pointHexer.write(repoint, iteratorForField);
            iteratorForField.advanceTo(repoint.getLocation());

            object.setPointer(repoint);
            object.setReferent(po.getObject());
            writeObject(iteratorForField, object, hexFramework);
        } else {
            writeObject(iterator, object, hexFramework);
        }
    }

    /**
     * Write the object that was be wrapped in the PointerObject, if it was.
     * @param iterator The iterator to use
     * @param object The FieldObject being used
     * @param hexFramework The framework being used
     * @return The created object
     */
    void writeObject(HexFieldIterator iterator, FieldObject object, HexFramework hexFramework);
}
