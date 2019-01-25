package com.github.lucbui.framework;

/**
 * A factory which encapsulates loading of readers/writers
 */
public interface FrameworkFactory {

    /**
     * Add all the readers to the builder object.
     * @param builder
     */
    void configure(HexFramework.Builder builder);
}
