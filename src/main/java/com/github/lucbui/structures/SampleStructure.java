package com.github.lucbui.structures;

import com.github.lucbui.annotations.AfterConstruct;
import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.annotations.StructField;
import com.github.lucbui.annotations.StructFieldType;
import com.github.lucbui.bytes.UnsignedWord;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * A sample data structure, to practice working with the @DataStructure annotation.
 */
@DataStructure
public class SampleStructure {

    @StructField(value=0, fieldType = StructFieldType.POINTER)
    private UnsignedWord word;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @AfterConstruct
    private void test(){
        System.out.println("Hello World!");
    }
}
