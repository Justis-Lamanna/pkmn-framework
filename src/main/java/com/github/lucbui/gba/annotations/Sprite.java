package com.github.lucbui.gba.annotations;

import com.github.lucbui.gba.gfx.BitDepth;
import com.github.lucbui.gba.gfx.SpriteSize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Denotes additional information for sprites
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Sprite {
    /**
     * Size of the sprite
     * @return The Sprite's size
     */
    SpriteSize size();

    /**
     * The sprite's bit depth
     * @return THe sprite's bit depth
     */
    BitDepth bitDepth() default BitDepth.FOUR;
}
