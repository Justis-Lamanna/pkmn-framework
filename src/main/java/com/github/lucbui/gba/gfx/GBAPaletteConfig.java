package com.github.lucbui.gba.gfx;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.ReflectionAnnotationFunction;
import com.github.lucbui.framework.RepointStrategy;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GBAPaletteConfig {
    int size();

    class Annotation implements ReflectionAnnotationFunction{

        @Override
        public Object onRead(Object obj, Field field, HexFieldIterator iterator) {
            int size = field.getAnnotation(GBAPaletteConfig.class).size();
            return GBAPalette.getHexReader(size).read(iterator);
        }

        @Override
        public boolean onWrite(Object obj, Field field, HexFieldIterator iterator){
            int size = field.getAnnotation(GBAPaletteConfig.class).size();
            GBAPalette.getHexWriter(size).write((GBAPalette) obj, iterator);
            return true;
        }
    }
}
