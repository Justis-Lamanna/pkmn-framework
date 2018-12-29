package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.WritePipe;

/**
 * A simple pipe which prints the object being passed through it.
 * @param <T>
 */
public class PrintPipe<T> implements ReadPipe<T>, WritePipe<T> {
    @Override
    public void read(T object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        System.out.println(object.toString());
    }

    @Override
    public void write(HexFieldIterator iterator, T object, PkmnFramework pkmnFramework) {
        System.out.println(object.toString());
    }
}
