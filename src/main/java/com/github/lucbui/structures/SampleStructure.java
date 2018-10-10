package com.github.lucbui.structures;

import com.github.lucbui.annotations.AfterRead;
import com.github.lucbui.annotations.BeforeWrite;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.StructField;
import com.github.lucbui.file.GBAPointer;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A sample data structure, to practice working with the @DataStructure annotation.
 */
@DataStructure(size = 8)
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

    @AfterRead
    private void test(){
        System.out.println("Hello World!");
    }

    @BeforeWrite
    private void test2(){
        System.out.println("Goodbye World!");
    }
}
