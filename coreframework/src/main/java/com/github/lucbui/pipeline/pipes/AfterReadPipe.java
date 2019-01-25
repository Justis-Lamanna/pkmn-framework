package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.annotations.AfterRead;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.HexFramework;
import com.github.lucbui.pipeline.ReadPipe;
import com.github.lucbui.pipeline.exceptions.ReadPipeException;
import org.apache.commons.lang3.reflect.MethodUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class AfterReadPipe implements ReadPipe<Object> {
    @Override
    public void read(Object object, HexFieldIterator iterator, HexFramework hexFramework) {
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(object.getClass(), AfterRead.class);
        for(Method method : methods){
            try {
                if(method.getParameterCount() == 0) {
                    MethodUtils.invokeMethod(object, true, method.getName());
                } else if(method.getParameterCount() == 1){
                    MethodUtils.invokeMethod(object, true, method.getName(), iterator);
                } else {
                    throw new ReadPipeException("AfterRead annotated method must have 0 or 1 parameters");
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new ReadPipeException("Error invoking method " + method.getName(), e);
            }
        }
    }
}
