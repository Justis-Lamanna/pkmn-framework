package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.DoublePipe;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.WritePipe;

import java.util.function.Function;

/**
 * A simple pipe which prints the object being passed through it.
 */
public class PrintPipe implements DoublePipe<Object> {

    Function<Object, String> stringFunction;

    /**
     * Pipe which calls toString() on the supplied object
     */
    public PrintPipe(){
        this.stringFunction = Object::toString;
    }

    /**
     * Pipe which prints a constant string
     * @param string The string to print upon invocation
     */
    public PrintPipe(String string){
        this.stringFunction = (i -> string);
    }

    /**
     * Pipe which converts an object into a string through a supplied function
     * @param stringFunction The function to convert object to string.
     */
    public PrintPipe(Function<Object, String> stringFunction){
        this.stringFunction = stringFunction;
    }

    @Override
    public void read(Object object, HexFieldIterator iterator, PkmnFramework pkmnFramework) {
        System.out.println(stringFunction.apply(object));
    }

    @Override
    public void write(HexFieldIterator iterator, Object object, PkmnFramework pkmnFramework) {
        System.out.println(stringFunction.apply(object));
    }
}
