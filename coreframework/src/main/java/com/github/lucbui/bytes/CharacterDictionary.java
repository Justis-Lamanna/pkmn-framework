package com.github.lucbui.bytes;

import com.github.lucbui.utility.CollectorUtils;
import com.github.lucbui.utility.Try;

import java.util.*;

/**
 * A dictionary that converts bytes and characters.
 * Each entry in the dictionary has a table mapping bytes to their displayable characters, and back. Each byte may
 * also be associated with additional characters, for ease of parsing. As an example, imagine a CharacterDictionary
 * with 193 being mapped to both Á and [A']. When bytes are converted to a string, the main representation is displayed
 * (In this case, Á). When a string is converted into a byte, however, both Á and [A'] are parsed into the same byte, 193.
 *
 * Note that, when defining a CharacterDictionary, particularly when parsing strings into bytes, great
 * care must be taken to make sure each character is unique. Furthermore, all prefixes must be unique too. For example,
 * if you have a character "[" and a character "[NUL]", the parsing algorithm would be unable to determine if you mean
 * the string "[", "N", "U", "L", "]", or the string "[NUL]". One solution is to prefix one of the characters with
 * a "\" character (Make sure, if the "\" alone is also a viable character, to escape it with "\\").
 */
public class CharacterDictionary {
    /**
     * ASCII CharacterDictionary
     * The control characters are as \, followed by [, their two-or-three digit name, then ].
     * Backslash is rendered as \\.
     */
    public static final CharacterDictionary ASCII = CharacterDictionary.startingAt(0)
            //Control Characters
            .set("\\[NUL]").set("\\[SOH]").set("\\[STX]").set("\\[ETX]")
            .set("\\[EOT]").set("\\[ENQ]").set("\\[ACK]").set("\\[BEL]")
            .set("\\[BS]").set("\\[HT]").set("\\[LF]").set("\\[VT]")
            .set("\\[FF]").set("\\[CR]").set("\\[SO]").set("\\[SI]").set("\\[DLE]")
            .set("\\[DC1]").set("\\[DC2]").set("\\[DC3]").set("\\[DC4]")
            .set("\\[NAK]").set("\\[SYN]").set("\\[ETB]").set("\\[CAN]")
            .set("\\[EM]").set("\\[SUB]").set("\\[ESC]").set("\\[FS]")
            .set("\\[GS]").set("\\[RS]").set("\\[US]")
            //
            .set(" ").set("!").set("\"").set("#")
            .set("$").set("%").set("&").set("'")
            .set("(").set(")").set("*").set("+")
            .set(",").set("-").set(".").set("/")
            //Numbers
            .set("0").set("1").set("2").set("3").set("4")
            .set("5").set("6").set("7").set("8").set("9")
            //
            .set(":").set(";").set("<").set("=")
            .set(">").set("?").set("@")
            //Uppercase
            .set("A").set("B").set("C").set("D")
            .set("E").set("F").set("G").set("H")
            .set("I").set("J").set("K").set("L")
            .set("M").set("N").set("O").set("P")
            .set("Q").set("R").set("S").set("T")
            .set("U").set("V").set("W").set("X").set("Y").set("Z")
            //
            .set("[").set("\\\\").set("]").set("^")
            .set("_").set("`")
            //Lowercase
            .set("a").set("b").set("c").set("d")
            .set("e").set("f").set("g").set("h")
            .set("i").set("j").set("k").set("l")
            .set("m").set("n").set("o").set("p")
            .set("q").set("r").set("s").set("t")
            .set("u").set("v").set("w").set("x").set("y").set("z")
            //
            .set("{").set("|").set("}").set("~")
            .set("\\[DEL]")
            .build();

    private Map<Byte, String> byteToChar;
    private Map<String, Byte> charToByte;

    /**
     * Create an empty map of characters to bytes
     */
    public CharacterDictionary(){
        byteToChar = new TreeMap<>();
        charToByte = new TreeMap<>();
    }

    /**
     * Make a copy of another CharacterDictionary
     * @param toCopy
     */
    public CharacterDictionary(CharacterDictionary toCopy){
        byteToChar = new TreeMap<>(toCopy.byteToChar);
        charToByte = new TreeMap<>(toCopy.charToByte);
    }

    /**
     * Construct a CharacterDictionary using a builder
     * @param idx The character to start at
     * @return A builder to construct the instance
     */
    public static Builder startingAt(int idx){
        return new Builder(idx);
    }

    /**
     * Add a byte-character association
     * @param bite The byte to register
     * @param mainChar The main character to associate with this byte
     * @param addlChars Additional characters that convert into that byte
     */
    private void set(byte bite, String mainChar, String... addlChars){
        Objects.requireNonNull(mainChar);
        Arrays.stream(addlChars).forEach(Objects::requireNonNull);

        byteToChar.put(bite, mainChar);
        charToByte.put(mainChar, bite);
        for(String addlChar : addlChars){
            charToByte.put(addlChar, bite);
        }
    }

    /**
     * Add a byte-character association
     * @param byteAsInt The byte to register
     * @param mainChar The main character to associate with this byte
     * @param addlChars Additional characters that convert into that byte
     */
    private void set(int byteAsInt, String mainChar, String... addlChars){
        Objects.requireNonNull(mainChar);
        Arrays.stream(addlChars).forEach(Objects::requireNonNull);
        byte bite = (byte)byteAsInt;

        byteToChar.put(bite, mainChar);
        charToByte.put(mainChar, bite);
        for(String addlChar : addlChars){
            charToByte.put(addlChar, bite);
        }
    }

    /**
     * Get the character for the supplied byte
     * @param bite The byte to retrieve
     * @return An optional containing its character representation, or empty if there is no entry
     */
    public Optional<String> getChar(byte bite){
        return Optional.ofNullable(byteToChar.get(bite));
    }

    /**
     * Get the byte for the supplied character
     * @param str The character to retrieve
     * @return An optional containing its byte representation, or empty if there is no entry
     */
    public Optional<Byte> getByte(String str){
        return Optional.ofNullable(charToByte.get(str));
    }

    /**
     * Get the byte for the supplied character
     * @param chr The character to retrieve
     * @return An optional containing its byte representation, or empty if there is no entry
     */
    public Optional<Byte> getByte(char chr){
        return Optional.ofNullable(charToByte.get(chr + ""));
    }

    /**
     * Test if this dictionary is complete.
     * If all bytes have a valid conversion, the dictionary is considered complete
     * @return True if the dictionary is complete
     */
    public boolean isComplete(){
        return byteToChar.size() == 0x100;
    }

    /**
     * Test if the byte has a string representation
     * @param bite The byte to search
     * @return True if the byte has a matching char
     */
    public boolean hasChar(byte bite){
        return byteToChar.containsKey(bite);
    }

    /**
     * Test if the string has a byte representation
     * @param str The string to search
     * @return True if the char has a matching byte
     */
    public boolean hasByte(String str){
        return charToByte.containsKey(str);
    }

    /**
     * Modify this CharacterDictionary
     * @return A builder, preloaded with this dictionary, for modifying.
     */
    public Builder modify(){
        return new Builder(this);
    }

    /**
     * Parse string into an array of bytes
     * This is a rudimentary parsing, which performs the following steps repeated:
     * 1. Checks the first character of the string for a match. If no match, the next character is included in the
     * search, and so on.
     * 2. Upon a match, the byte is appended, and the matched portion of the word removed.
     * @param str The string to parse
     * @return The parsed byte object
     */
    public Try<byte[]> parse(String str){
        Objects.requireNonNull(str);
        List<Byte> bytes = new ArrayList<>();
        int cursor = 1;
        String workingString = str;
        while(!workingString.isEmpty()){
            String cur = workingString.substring(0, cursor);
            if(hasByte(cur)){
                byte bite = getByte(cur).orElseThrow(RuntimeException::new); //Should never throw an exception.
                bytes.add(bite);
                workingString = workingString.substring(cursor);
                cursor = 1;
            } else {
                cursor++;
                if(cursor > workingString.length()){
                    return Try.error("Unable to parse string " + str);
                }
            }
        }
        return Try.ok(bytes.stream().collect(CollectorUtils.toByteArray()));
    }

    /**
     * A Builder to construct CharacterDictionaries
     */
    public static class Builder {
        private CharacterDictionary characterDictionary;
        private int current;

        private Builder(int startAt){
            current = startAt;
            characterDictionary = new CharacterDictionary();
        }

        private Builder(CharacterDictionary characterDictionary){
            current = 0;
            characterDictionary = new CharacterDictionary(characterDictionary);
        }

        /**
         * Set the byte to these characters, and advance the cursor
         * @param main The main character
         * @param addl Additional characters to use
         * @return This builder, to chain
         */
        public Builder set(String main, String... addl){
            characterDictionary.set(current, main, addl);
            current++;
            return this;
        }

        /**
         * Set the byte to these characters, and move the cursor to bite
         * @param bite The byte to set
         * @param main The main character
         * @param addl Additional characters to use
         * @return This builder, to chain
         */
        public Builder set(int bite, String main, String... addl){
            characterDictionary.set(bite, main, addl);
            current = bite;
            return this;
        }

        /**
         * Register additional characters for use with this byte
         * @param bite The byte to use
         * @param addlChars The additional characters to use
         * @return This builder, to chain
         */
        public Builder addAdditional(int bite, String... addlChars){
            for(String addlChar : addlChars){
                characterDictionary.charToByte.put(addlChar, (byte)bite);
            }
            return this;
        }

        /**
         * Build the CharacterDictionary
         * @return The constructed dictionary.
         */
        public CharacterDictionary build(){
            return characterDictionary;
        }

        /**
         * Build the CharacterDictionary, only if it is complete
         * @return A populated optional if complete, and empty if not.
         */
        public Optional<CharacterDictionary> buildIfFinished(){
            if(characterDictionary.isComplete()){
                return Optional.of(characterDictionary);
            } else {
                return Optional.empty();
            }
        }
    }
}
