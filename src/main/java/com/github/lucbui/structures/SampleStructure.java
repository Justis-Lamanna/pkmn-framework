package com.github.lucbui.structures;

import com.github.lucbui.annotations.AfterConstruct;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.StructField;
import com.github.lucbui.annotations.StructFieldType;
import com.github.lucbui.bytes.UnsignedWord;
import com.github.lucbui.file.GBAPointer;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A sample data structure, to practice working with the @DataStructure annotation.
 */
@DataStructure
public class SampleStructure {

    @StructField(0)
    private GBAPointer ptr1;

    @StructField(4)
    private GBAPointer prt2;

    public SampleStructure(){

    }

    public SampleStructure(GBAPointer ptr1, GBAPointer prt2) {
        this.ptr1 = ptr1;
        this.prt2 = prt2;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @AfterConstruct
    private void test(){
        System.out.println("Hello World!");
    }
}
