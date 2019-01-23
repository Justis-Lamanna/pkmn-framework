package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.BeforeWrite;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.PkmnFramework;
import com.github.lucbui.pipeline.WritePipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import com.github.lucbui.pipeline.exceptions.WritePipeException;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class BeforeWritePipe implements WritePipe<Object> {
    @Override
    public void write(HexFieldIterator iterator, Object object, PkmnFramework pkmnFramework) {
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(object.getClass(), BeforeWrite.class);
        for(Method method : methods){
            try {
                if (method.getParameterCount() == 0) {
                    MethodUtils.invokeMethod(object, true, method.getName());
                } else if(method.getParameterCount() == 1){
                    MethodUtils.invokeMethod(object, true, method.getName(), iterator);
                } else {
                    throw new WritePipeException("BeforeWrite annotated method must have 0 or 1 parameters");
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new ReadPipeException("Error invoking method " + method.getName(), e);
            }
        }
    }
}
