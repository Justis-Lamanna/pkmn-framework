package com.github.lucbui.pipeline.pipes;

import com.github.lucbui.pipeline.CreatePipe;
import com.github.lucbui.pipeline.exceptions.CreatePipeException;

public class EmptyConstructorCreatePipe implements CreatePipe {

    @Override
    public <T> T create(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CreatePipeException("Unable to instantiate class", e);
        }
    }
}
