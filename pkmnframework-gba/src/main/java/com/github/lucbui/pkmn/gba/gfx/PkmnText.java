package com.github.lucbui.pkmn.gba.gfx;

import com.github.lucbui.bytes.CharacterDictionary;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PkmnText {

    /**
     * Converts a byte sequence to a sequence of \h[hex number] strings.
     */
    private static final Function<List<Integer>, Optional<String>> TO_BYTE_REPRESENTATION =
            li -> Optional.of(li.stream().map(i -> "\\h" + String.format("%02x", i)).collect(Collectors.joining()));

    public static final CharacterDictionary CHARS = CharacterDictionary.startingAt(0, Collections.singletonList(TO_BYTE_REPRESENTATION))
            //0
            .set(" ").set("À").set("Á").set("Â")
            .set("Ç").set("È").set("É").set("Ê")
            .set("Ë").set("Ì").skip(  ).set("Î")
            .set("Ï").set("Ò").set("Ó").set("Ô")
            //1
            .set("Œ").set("Ù").set("Ú").set("Û")
            .set("Ñ").set("ß").set("à").set("á")
            .skip(  ).set("ç").set("è").set("é")
            .set("ê").set("ë").set("ì").skip(  )
            //2
            .set("î").set("ï").set("ò").set("ó")
            .set("ô").set("œ").set("ù").set("ú")
            .set("û").set("ñ").set("º").set("ª")
            .set("ᵉʳ").set("&").set("+").skip()
            //3
            .skip().skip().skip().skip()
            .set("[Lv]").set("=").set(";").skip()
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            //4
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            //5
            .skip().set("¿").set("¡").set("[pk]")
            .set("[mn]").set("[po]").set("[ke]").set("[bl]")
            .set("[oc]").set("[k]").set("Í").set("%")
            .set("(").set(")").skip().skip()
            //6
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            .set("â").skip().skip().skip()
            .skip().skip().skip().set("í")
            //7
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            .skip().set("[up]").set("[down]").set("[left]")
            .set("[right]").set("[space1]").set("[space2]").set("[space3]")
            //8
            .set("[space4]").set("[space5]").set("[space6]").set("[space7]")
            .set("ᵉ").set("<").set(">").skip()
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            //9
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            .skip().skip().skip().skip()
            //A
            .set("ʳᵉ").set("0").set("1").set("2")
            .set("3").set("4").set("5").set("6")
            .set("7").set("8").set("9").set("!")
            .set("?").set(".").set("-").set("・")
            //B
            .set("[.]").set("\"").set("[\"]").set("'")
            .set("[']").set("♂").set("♀").set("[$]")
            .set(",").set("×").set("/").set("A")
            .set("B").set("C").set("D").set("E")
            //C
            .set("F").set("G").set("H").set("I")
            .set("J").set("K").set("L").set("M")
            .set("N").set("O").set("P").set("Q")
            .set("R").set("S").set("T").set("U")
            //D
            .set("V").set("W").set("X").set("Y")
            .set("Z").set("a").set("b").set("c")
            .set("d").set("e").set("f").set("g")
            .set("h").set("i").set("j").set("k")
            //E
            .set("l").set("m").set("n").set("o")
            .set("p").set("q").set("r").set("s")
            .set("t").set("u").set("v").set("w")
            .set("x").set("y").set("z").set("[triangle]")
            //F
            .set(":").set("Ä").set("Ö").set("Ü")
            .set("ä").set("ö").set("ü").set("\\r")
            .set("\\p")
            //FC Control Characters
            .skip()
            //FD Control Characters
            .skip()
            //FE - FF
            .set("\\n").skip()
            //FD control chars
            .set(Arrays.asList(0xFD, 0x01), "[player]")
            .set(Arrays.asList(0xFD, 0x02), "[buffer1]")
            .set(Arrays.asList(0xFD, 0x03), "[buffer2]")
            .set(Arrays.asList(0xFD, 0x04), "[buffer3]")
            .set(Arrays.asList(0xFD, 0x06), "[rival]")
            .set(Arrays.asList(0xFD, 0x07), "[game]")
            .set(Arrays.asList(0xFD, 0x08), "[team]")
            .set(Arrays.asList(0xFD, 0x09), "[otherteam]")
            .set(Arrays.asList(0xFD, 0x0A), "[boss]")
            .set(Arrays.asList(0xFD, 0x0B), "[otherboss]")
            .set(Arrays.asList(0xFD, 0x0C), "[legendary]")
            .set(Arrays.asList(0xFD, 0x0D), "[otherlegendary]")
            .build();


}
