package com.github.lucbui.bytes;

import com.github.lucbui.utility.CollectorUtils;
import com.github.lucbui.utility.Try;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A dictionary that converts bytes and characters.
 * Each entry in the dictionary has a table mapping byte sequences to their displayable characters, and back. Each byte sequence may
 * also be associated with additional characters, for ease of parsing. As an example, imagine a CharacterDictionary
 * with 193 being mapped to both Á and [A']. When the byte sequence is converted to a string, the main representation is displayed
 * (In this case, Á). When a string is converted into a byte sequence, however, both Á and [A'] are parsed into the same byte, 193.
 *
 * In addition, each character may be associated with a number of bytes, rather than just one.
 *
 * Note that, when defining a CharacterDictionary, great
 * care must be taken to make sure each character, and their prefixes, are unique. For example,
 * if you have a character "[" and a character "[NUL]", the parsing algorithm would be unable to determine if you mean
 * the string "[", "N", "U", "L", "]", or the character "[NUL]". One solution is to prefix one of the characters with
 * a "\" character (Make sure, if the "\" alone is also a viable character, to escape it with "\\"). Similar care must
 * be taken with byte sequences: You should not register a character for [0x00] and [0x00 0x01].
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

    private Map<List<Byte>, String> byteToChar;
    private Map<String, List<Byte>> charToByte;

    /**
     * Create an empty map of characters to bytes
     */
    private CharacterDictionary(){
        byteToChar = new HashMap<>();
        charToByte = new TreeMap<>();
    }

    /**
     * Make a copy of another CharacterDictionary
     * @param toCopy
     */
    public CharacterDictionary(CharacterDictionary toCopy){
        byteToChar = new HashMap<>(toCopy.byteToChar);
        charToByte = new TreeMap<>(toCopy.charToByte);
    }

    /**
     * Construct a CharacterDictionary using a builder
     * @param idx The character to start at
     * @param addlFunctions Additional functions, applied to each set() invokation, that generate alternative
     *                      string representations from their byte form.
     * @return A builder to construct the instance
     */
    public static Builder startingAt(int idx, List<Function<List<Integer>, Optional<String>>> addlFunctions){
        return new Builder(idx, addlFunctions);
    }

    /**
     * Construct a CharacterDictionary using a builder
     * @param idx The character to start at
     * @return A builder to construct the instance
     */
    public static Builder startingAt(int idx){
        return new Builder(idx, null);
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

        byteToChar.put(Collections.singletonList(bite), mainChar);
        charToByte.put(mainChar, Collections.singletonList(bite));
        for(String addlChar : addlChars){
            charToByte.put(addlChar, Collections.singletonList(bite));
        }
    }

    /**
     * Add a byte sequence-character association
     * @param bites The bytes to register
     * @param mainChar The main character to associate with this byte
     * @param addlChars Additional characters that convert into that byte
     */
    private void set(List<Byte> bites, String mainChar, String... addlChars){
        Objects.requireNonNull(bites);
        Objects.requireNonNull(mainChar);
        bites.forEach(Objects::requireNonNull);
        Arrays.stream(addlChars).forEach(Objects::requireNonNull);

        byteToChar.put(bites, mainChar);
        charToByte.put(mainChar, bites);
        for(String addlChar : addlChars){
            charToByte.put(addlChar, bites);
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

        byteToChar.put(Collections.singletonList(bite), mainChar);
        charToByte.put(mainChar, Collections.singletonList(bite));
        for(String addlChar : addlChars){
            charToByte.put(addlChar, Collections.singletonList(bite));
        }
    }

    /**
     * Get the character for the supplied byte
     * @param bite The byte to retrieve
     * @return An optional containing its character representation, or empty if there is no entry
     */
    public Optional<String> getChar(byte bite){
        return Optional.ofNullable(byteToChar.get(Collections.singletonList(bite)));
    }

    /**
     * Get the character for the supplied byte sequence
     * @param bites The bytes to retrieve
     * @return An optional containing its character representation, or empty if there is no entry
     */
    public Optional<String> getChar(List<Byte> bites){
        return Optional.ofNullable(byteToChar.get(bites));
    }

    /**
     * Get the byte for the supplied character
     * @param str The character to retrieve
     * @return An optional containing its byte representation, or empty if there is no entry
     */
    public Optional<List<Byte>> getByte(String str){
        return Optional.ofNullable(charToByte.get(str));
    }

    /**
     * Get the byte for the supplied character
     * @param chr The character to retrieve
     * @return An optional containing its byte representation, or empty if there is no entry
     */
    public Optional<List<Byte>> getByte(char chr){
        return Optional.ofNullable(charToByte.get(chr + ""));
    }

    /**
     * Test if the byte has a string representation
     * @param bite The byte to search
     * @return True if the byte has a matching char
     */
    public boolean hasChar(byte bite){
        return byteToChar.containsKey(Collections.singletonList(bite));
    }

    /**
     * Test if the byte sequence has a char representation
     * @param bites The bytes to search
     * @return True if the bytes have a matching char
     */
    public boolean hasChar(List<Byte> bites){
        return byteToChar.containsKey(bites);
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
    public Try<List<Byte>> parse(String str){
        Objects.requireNonNull(str);
        List<Byte> bytes = new ArrayList<>();
        int cursor = 1;
        String workingString = str;
        while(!workingString.isEmpty()){
            String cur = workingString.substring(0, cursor);
            if(hasByte(cur)){
                List<Byte> bite = getByte(cur).orElseThrow(RuntimeException::new); //Should never throw an exception.
                bytes.addAll(bite);
                workingString = workingString.substring(cursor);
                cursor = 1;
            } else {
                cursor++;
                if(cursor > workingString.length()){
                    return Try.error("Unable to parse string " + workingString);
                }
            }
        }
        return Try.ok(bytes);
    }

    /**
     * Parse bytes into a String
     * This is a rudimentary parsing, which performs the following steps repeated:
     * 1. Checks the first byte of the byte sequence for a match. If no match, the next byte is included in the
     * search, and so on.
     * 2. Upon a match, the string is appended, and the matched portion of the byte sequence removed.
     * @param bites The bytes to parse
     * @return The parsed String
     */
    public Try<String> parse(List<Byte> bites){
        Objects.requireNonNull(bites);
        bites.forEach(Objects::requireNonNull);
        StringBuilder sb = new StringBuilder();
        int cursor = 1;
        List<Byte> workingBytes = bites;
        while(!workingBytes.isEmpty()){
            List<Byte> curBites = workingBytes.subList(0, cursor);
            if(hasChar(curBites)){
                String str = getChar(curBites).orElseThrow(RuntimeException::new); //Should never happen
                sb.append(str);
                workingBytes = workingBytes.subList(cursor, workingBytes.size());
            } else {
                cursor++;
                if(cursor > workingBytes.size()){
                    return Try.error("Unable to parse bytes " + workingBytes.toString());
                }
            }
        }
        return Try.ok(sb.toString());
    }

    /**
     * Parse bytes into a String
     * This is a rudimentary parsing, which performs the following steps repeated:
     * 1. Checks the first byte of the byte sequence for a match. If no match, the next byte is included in the
     * search, and so on.
     * 2. Upon a match, the string is appended, and the matched portion of the byte sequence removed.
     * @param bites The bytes to parse
     * @return The parsed String
     */
    public Try<String> parse(byte... bites){
        Objects.requireNonNull(bites);
        List<Byte> bitesAsList = new ArrayList<>();
        for(byte bite : bites){
            bitesAsList.add(bite);
        }
        return parse(bitesAsList);
    }

    @Override
    public String toString(){
        return byteToChar.keySet().stream().map(bite -> {
            List<String> additionals = charToByte.entrySet().stream().filter(i -> i.getValue().equals(bite)).map(Map.Entry::getKey).collect(Collectors.toList());
            return bite.toString() + "->" + additionals;
        }).collect(Collectors.joining(",", "[", "]"));
    }

    /**
     * A Builder to construct CharacterDictionaries
     */
    public static class Builder {
        private CharacterDictionary characterDictionary;
        private int current;
        private List<Function<List<Integer>, Optional<String>>> addlFuncs;

        private Builder(int startAt, List<Function<List<Integer>, Optional<String>>> addlFuncs){
            current = startAt;
            characterDictionary = new CharacterDictionary();
            this.addlFuncs = addlFuncs;
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
            String[] addlConcated;
            if(addlFuncs == null){
                addlConcated = addl;
            } else {
                addlConcated = Stream.concat(
                        Arrays.stream(addl),
                        addlFuncs.stream().flatMap(
                                i -> CollectorUtils.toStream(i.apply(Collections.singletonList(current)))))
                        .toArray(String[]::new);
            }
            characterDictionary.set(current, main, addlConcated);
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
            String[] addlConcated;
            if(addlFuncs == null){
                addlConcated = addl;
            } else {
                addlConcated = Stream.concat(
                        Arrays.stream(addl),
                        addlFuncs.stream().flatMap(
                                i -> CollectorUtils.toStream(i.apply(Collections.singletonList(bite)))))
                        .toArray(String[]::new);
            }
            characterDictionary.set(bite, main, addlConcated);
            current = bite;
            return this;
        }

        /**
         * Set the byte sequence to these characters
         * @param bitesAsInt The bytes, as integers
         * @param main The main character
         * @param addl Additional characters to use
         * @return This builder, to chain
         */
        public Builder set(List<Integer> bitesAsInt, String main, String... addl){
            List<Byte> bites = bitesAsInt.stream().map(Integer::byteValue).collect(Collectors.toList());
            String[] addlConcated;
            if(addlFuncs == null){
                addlConcated = addl;
            } else {
                addlConcated = Stream.concat(
                        Arrays.stream(addl),
                        addlFuncs.stream().flatMap(
                                i -> CollectorUtils.toStream(i.apply(bitesAsInt))))
                        .toArray(String[]::new);
            }
            characterDictionary.set(bites, main, addlConcated);
            return this;
        }

        /**
         * Skip the next byte definition
         * The addlFuncs are still invoked, if they are present. The first return becomes the main string
         * representation, and any additional become alternates.
         * @return This builder, to chain
         */
        public Builder skip(){
            if(addlFuncs != null) {
                String[] addl =  addlFuncs.stream().flatMap(
                        i -> CollectorUtils.toStream(i.apply(Collections.singletonList(current))))
                        .toArray(String[]::new);
                if (addl.length > 0) {
                    if (addl.length > 1) {
                        String[] addlAddls = Arrays.copyOfRange(addl, 1, addl.length);
                        characterDictionary.set(current, addl[0], addlAddls);
                    } else {
                        characterDictionary.set(current, addl[0]);
                    }
                }
            }
            current++;
            return this;
        }

        /**
         * Skip the next byte definition, without invoking extra functions
         * @return This builder, to chain
         */
        public Builder skipAndSkipFuncs(){
            current++;
            return this;
        }

        /**
         * Build the CharacterDictionary
         * @return The constructed dictionary.
         */
        public CharacterDictionary build(){
            return characterDictionary;
        }
    }
}
