package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.DoublePipe;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.WritePipe;

/**
 * A simple pipe which prints the object being passed through it.
 */
public class PrintPipe implements DoublePipe<Object> {
    @Override
    public void read(Object object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        System.out.println(object.toString());
    }

    @Override
    public void write(HexFieldIterator iterator, Object object, PkmnFramework pkmnFramework) {
        System.out.println(object.toString());
    }
}
