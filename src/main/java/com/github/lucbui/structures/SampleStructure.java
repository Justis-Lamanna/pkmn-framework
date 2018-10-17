package com.github.lucbui.structures;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.PointerField;
import com.github.lucbui.annotations.StructField;
import com.github.lucbui.annotations.TestAnnot;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.bytes.UnsignedWord;
import com.github.lucbui.gba.GBAPointer;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A sample data structure, to practice working with the @DataStructure annotation.
 */
@DataStructure(size = 8)
public class SampleStructure {

    @StructField(value = 0)
    @PointerField(objectType = UnsignedWord.class)
    private PointerObject<GBAPointer, UnsignedWord> ptr1;

    @StructField(value = 4)
    @TestAnnot
    private GBAPointer ptr2;

    public SampleStructure(){

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
