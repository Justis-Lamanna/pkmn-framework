package com.github.lucbui.framework;

import com.github.lucbui.bytes.RepointMetadata;
import com.github.lucbui.bytes.RepointStrategy;
import com.github.lucbui.file.Pointer;

public class NoRepointStrategy implements RepointStrategy {
    @Override
    public Pointer repoint(RepointMetadata metadata) {
        throw new IllegalStateException("Cannot repoint.");
    }
}
