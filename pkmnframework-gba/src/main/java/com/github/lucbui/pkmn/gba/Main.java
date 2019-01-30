package com.github.lucbui.pkmn.gba;

import com.github.lucbui.bytes.CharacterDictionary;

import java.util.Arrays;

public class Main {
    public static void main(String...args){
        System.out.println(Arrays.toString(CharacterDictionary.ASCII.parse("Hello World!").orThrow()));
    }
}
