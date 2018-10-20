package com.github.lucbui.gba.gfx;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.framework.ReflectionAnnotationFunction;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface GBATileConfig {
    GBATile.BitDepth bitDepth();

    class Annotation implements ReflectionAnnotationFunction {

        @Override
        public Object onRead(Object objToReadFrom, Field field, HexFieldIterator iterator) {
            GBATile.BitDepth bd = field.getAnnotation(GBATileConfig.class).bitDepth();
            return GBATile.getHexReader(bd).read(iterator);
        }

        @Override
        public boolean onWrite(Object objToWrite, Field field, HexFieldIterator iterator) {
            GBATile.HEX_WRITER.write((GBATile) objToWrite, iterator);
            return true;
        }
    }
}
