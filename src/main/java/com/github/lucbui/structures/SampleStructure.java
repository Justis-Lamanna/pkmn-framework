package com.github.lucbui.structures;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.StructField;
import com.github.lucbui.bytes.UnsignedByte;
import com.github.lucbui.bytes.UnsignedShort;
import com.github.lucbui.bytes.UnsignedWord;
import com.github.lucbui.file.GBAPointer;
import com.github.lucbui.file.Pointer;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A sample data structure, to practice working with the @DataStructure annotation.
 */
@DataStructure
public class SampleStructure {

    @StructField(0)
    public Pointer pointer;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
