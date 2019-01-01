package com.github.lucbui.pipeline;

/**
 * A pipe which functions both as a reader and a writer
 * @param <T>
 */
public interface DoublePipe<T> extends ReadPipe<T>, WritePipe<T> {
}
