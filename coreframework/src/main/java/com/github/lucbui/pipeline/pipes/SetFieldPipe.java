package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.FieldObject;
import com.github.lucbui.framework.HexFramework;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;

/**
 * A pipe which sets the fields in the FieldObject to the new value
 */
public class SetFieldPipe implements ReadPipe<FieldObject>  {
    @Override
    public void read(FieldObject object, HexFieldIterator iterator, HexFramework hexFramework) {
        object.set().orThrow(ReadPipeException::new);
    }
}
