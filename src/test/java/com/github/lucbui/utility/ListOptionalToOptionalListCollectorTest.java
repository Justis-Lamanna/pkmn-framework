package com.github.lucbui.utility;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class ListOptionalToOptionalListCollectorTest {

    @Test
    public void listOfNonEmptyOptionals() {
        List<Integer> testIntegers = Arrays.asList(10, 15, 20);
        List<Optional<Integer>> loi = testIntegers.stream().map(Optional::of).collect(Collectors.toList());
        Optional<List<Integer>> oli = loi.stream().collect(new ListOptionalToOptionalListCollector<>());
        assertEquals(testIntegers, oli.get());
    }

    @Test
    public void listOfNonEmptyWithOneEmptyOptionals(){
        List<Integer> testIntegers = Arrays.asList(10, 15, 20);
        List<Optional<Integer>> loi = testIntegers.stream().map(Optional::of).collect(Collectors.toList());
        loi.add(Optional.empty());
        Optional<List<Integer>> oli = loi.stream().collect(new ListOptionalToOptionalListCollector<>());
        assertTrue(!oli.isPresent());
    }
}