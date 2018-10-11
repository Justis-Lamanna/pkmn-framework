package com.github.lucbui.structures;

import com.github.lucbui.annotations.*;
import com.github.lucbui.bytes.UnsignedWord;
import com.github.lucbui.file.GBAPointer;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A sample data structure, to practice working with the @DataStructure annotation.
 */
@DataStructure(size = 8)
public class SampleStructure {

    @StructField(value = 0, fieldType = StructFieldType.POINTER)
    private UnsignedWord word1;

    @StructField(value = 0, readOnly = true)
    private GBAPointer ptr1;

    @StructField(value = 4, fieldType = StructFieldType.POINTER)
    private UnsignedWord prt2;

    @StructField(value = 4, readOnly = true)
    private GBAPointer ptr2;

    public SampleStructure(){

    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @AfterRead
    private void test(){
        System.out.println("Hello World!");
    }

    @BeforeWrite
    private void test2(){
        System.out.println("Goodbye World!");
    }
}
