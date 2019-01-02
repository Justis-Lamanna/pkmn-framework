package com.github.lucbui.strategy;

import com.github.lucbui.pipeline.exceptions.CreatePipeException;

/**
 * A creation strategy that creates an object using an empty constructor.
 */
public class EmptyConstructorCreateStrategy implements CreateStrategy {

    @Override
    public <T> T create(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new CreatePipeException("Unable to instantiate class", e);
        }
    }
}
