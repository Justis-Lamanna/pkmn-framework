package com.github.lucbui.pkmn.gba;

import com.github.lucbui.bytes.CharacterDictionary;

import java.util.List;

public class Main {
    public static void main(String...args){
        List<Byte> um = CharacterDictionary.ASCII.parse("Hello World!").orThrow();
        String back = CharacterDictionary.ASCII.parse(um).orThrow();
        System.out.println(um);
        System.out.println(back);
    }
}
