package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.Absolute;
import com.github.lucbui.annotations.Offset;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.HexFramework;
import com.github.lucbui.pipeline.DoublePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;

/**
 * A pipe which calculates the Offset of the object to read, and moves the iterator to that position.
 */
public class OffsetParsePipe implements DoublePipe<FieldObject> {
    @Override
    public void read(FieldObject object, HexFieldIterator iterator, HexFramework hexFramework) {
        object.setPointer(parseOffset(object, iterator, hexFramework));
        iterator.advanceTo(object.getPointer().getLocation());
    }

    @Override
    public void write(HexFieldIterator iterator, FieldObject object, HexFramework hexFramework) {
        object.setPointer(parseOffset(object, iterator, hexFramework));
        iterator.advanceTo(object.getPointer().getLocation());
    }

    //Calculate the offset of this field, given the @Offset annotation and optional @Absolute annotation
    private Pointer parseOffset(FieldObject object, HexFieldIterator iterator, HexFramework hexFramework){
        Offset offset = object.getAnnotation(Offset.class);
        long offsetAsLong = hexFramework.getEvaluator().evaluateLong(offset.value()).orElseThrow(ReadPipeException::new);

        if(object.isAnnotationPresent(Absolute.class)){
            return Pointer.of(offsetAsLong);
        } else {
            return Pointer.of(offsetAsLong + iterator.getPosition());
        }
    }
}
