package com.github.lucbui.bytes;

import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.utility.CollectorUtils;
import com.github.lucbui.utility.MathUtils;
import com.github.lucbui.utility.Try;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A string made of ASCII characters.
 */
public class ASCIIString {
    private byte[] string;

    private ASCIIString(byte[] string){
        this.string = string;
    }

    /**
     * Convert a regular string to an ASCIIString.
     * @param string The string to parse
     * @return The created string, wrapped in a Try
     */
    public static Try<ASCIIString> of(String string){
        if(string == null) return Try.error("string is null");
        byte[] biteArray = new byte[string.length()];
        for(int idx = 0; idx < biteArray.length; idx++){
            if(string.charAt(idx) > 127){
                return Try.error("Can't convert string " + string + ": invalid character " + string.charAt(idx));
            } else {
                biteArray[idx] = (byte)string.charAt(idx);
            }
        }
        return Try.ok(new ASCIIString(biteArray));
    }

    /**
     * Convert an array of bytes into an ASCIIString.
     * @param bytes The bytes to use
     * @return The created string, wrapped in a Try
     */
    public static Try<ASCIIString> of(byte[] bytes){
        if(bytes == null) return Try.error("string is null");
        byte[] biteArray = new byte[bytes.length];
        for(int idx = 0; idx < biteArray.length; idx++){
            if(bytes[idx] < 0){
                return Try.error("Can't convert bytes " + Arrays.toString(bytes) + ": invalid character " + bytes[idx]);
            } else {
                biteArray[idx] = bytes[idx];
            }
        }
        return Try.ok(new ASCIIString(biteArray));
    }

    /**
     * Get a Hexer which reads a fixed number of characters
     * @param length The length of the string
     * @return A Hexer which can read ASCIIStrings of the specified length.
     */
    public static Hexer<ASCIIString> hexerByLength(int length){
        MathUtils.assertNonNegative(length);
        return new Hexer<ASCIIString>() {
            @Override
            public int getSize(ASCIIString object) {
                return object.string.length;
            }

            @Override
            public ASCIIString read(HexFieldIterator iterator) {
                List<Byte> chars = new ArrayList<>(length);
                for(int idx = 0; idx < length; idx++) {
                    byte bite = iterator.getByte(0).orThrow();
                    chars.add(bite);
                    iterator.advanceRelative(1);
                }
                return new ASCIIString(chars.stream().collect(CollectorUtils.toByteArray()));
            }

            @Override
            public void write(ASCIIString object, HexFieldIterator iterator) {
                ByteWindow byteWindow = new ByteWindow();
                byteWindow.set(0, Arrays.copyOfRange(object.string, 0, length));
                iterator.write(byteWindow).orThrow();
            }
        };
    }

    /**
     * Get a Hexer which reads up until a delimiter.
     * Warning that, when writing a string of this type, it is easy to extend past the old string
     * and start overwriting data. In almost all cases, the ASCIIString should be inside a PointerObject,
     * to handle repoints correctly.
     * @param delimiter The delimiter to read to
     * @return A Hexer which can read ASCIIStrings up to the specified delimiter.
     */
    public static Hexer<ASCIIString> hexerByDelimiter(byte delimiter){
        return new Hexer<ASCIIString>() {
            @Override
            public int getSize(ASCIIString object) {
                return object.string.length;
            }

            @Override
            public ASCIIString read(HexFieldIterator iterator) {
                List<Byte> chars = new ArrayList<>();
                Byte bite;
                while((bite = iterator.getByte(0).orThrow()) != delimiter){
                    chars.add(bite);
                }
                return new ASCIIString(chars.stream().collect(CollectorUtils.toByteArray()));
            }

            @Override
            public void write(ASCIIString object, HexFieldIterator iterator) {
                ByteWindow byteWindow = new ByteWindow();
                byteWindow.set(0, object.string);
                byteWindow.set(object.string.length, delimiter);
                iterator.write(byteWindow).orThrow();
            }
        };
    }

    /**
     * Get this ASCIIString as a string
     * @return A string rendition of this ASCII string
     */
    public String getString(){
        return new String(string, StandardCharsets.UTF_8);
    }

    /**
     * Get this ASCIIString as an array of bytes
     * @return A byte rendition of this ASCII string
     */
    public byte[] getBytes(){
        return Arrays.copyOf(string, string.length);
    }
}
