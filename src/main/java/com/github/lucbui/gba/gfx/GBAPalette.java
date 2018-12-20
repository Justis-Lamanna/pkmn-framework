package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.file.HexFieldIterator;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A palette which holds a list of GBAColors
 */
public class GBAPalette implements Iterable<GBAColor>, Serializable {

    private static final long serialVersionUID = 42L;

    private List<GBAColor> colors;

    /**
     * Initialize a GBAPalette from a list of colors.
     * @param colors The colors to use.
     */
    private GBAPalette(List<GBAColor> colors){
        this.colors = new ArrayList<>(colors);
    }

    /**
     * Get the colors in this palette.
     * The returned collection is an immutable list containing all the colors in this palette.
     * @return The colors in this palette.
     */
    public List<GBAColor> getColors(){
        return new ArrayList<>(this.colors);
    }

    /**
     * Get a color in the palette.
     * @param slot The slot to retrieve.
     * @return The color retrieved.
     */
    public GBAColor get(int slot){
        return colors.get(slot);
    }

    /**
     * Get the first color
     * @return First color, or null if palette is empty.
     */
    public GBAColor getFirst(){
        return colors.get(0);
    }

    /**
     * Tests if a palette has a color at the specified slot.
     * @param slot The slot to view.
     * @return True if the palette has a color registered in that slot.
     */
    public boolean hasColor(int slot){
        return slot < colors.size();
    }

    /**
     * Get the size of the palette.
     * @return
     */
    public int size(){
        return colors.size();
    }

    @Override
    public String toString() {
        return "GBAPalette{" +
                "colors=" + colors +
                '}';
    }

    /**
     * Rotates a palette by some number of slots.
     * @param shift The number of slots to shift.
     * @return
     */
    public GBAPalette rotatePalette(int shift){
        List<GBAColor> newColors = new ArrayList<>(this.colors);
        Collections.rotate(newColors, shift);
        return new GBAPalette(newColors);
    }

    /**
     * Reverses a palette.
     * @return
     */
    public GBAPalette reversePalette(){
        List<GBAColor> newColors = new ArrayList<>(this.colors);
        Collections.reverse(newColors);
        return new GBAPalette(newColors);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GBAPalette that = (GBAPalette) o;
        return Objects.equals(colors, that.colors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(colors);
    }

    /**
     * Get a hexer to parse a palette.
     * @param numberOfColors The number of colors to read.
     * @return
     */
     public static Hexer<GBAPalette> getHexer(int numberOfColors){
        return new Hexer<GBAPalette>() {
            @Override
            public int getSize(GBAPalette object) {
                //The size of a palette is equal to the size of each individual color.
                return object.getColors()
                        .stream()
                        .mapToInt(i -> GBAColor.HEXER.getSize(i))
                        .sum();
            }

            @Override
            public GBAPalette read(HexFieldIterator iterator) {
                List<GBAColor> colors = new ArrayList<>();
                for(int count = 0; count < numberOfColors; count++){
                    colors.add(GBAColor.HEXER.read(iterator));
                    iterator.advanceRelative(GBAColor.HEXER.getSize(colors.get(count)));
                }
                return new GBAPalette(colors);
            }

            @Override
            public void write(GBAPalette object, HexFieldIterator iterator) {
                for(int idx = 0; idx < numberOfColors; idx++){
                    GBAColor color = object.get(idx);
                    GBAColor.HEXER.write(color, iterator);
                    iterator.advanceRelative(GBAColor.HEXER.getSize(color));
                }
            }
        };
    }

    /**
     * Initialize a builder to easily generate palettes.
     * @return
     */
    public static Creator create(){
        return new Creator();
    }

    public Creator modify(){
        return new Creator(this);
    }

    //Functions that allow this palette to be used identically to a list of colors.

    @Override
    public Iterator<GBAColor> iterator() {
        return colors.iterator();
    }

    @Override
    public Spliterator<GBAColor> spliterator(){
        return colors.spliterator();
    }

    @Override
    public void forEach(Consumer<? super GBAColor> consumer){
        colors.forEach(consumer);
    }

    /**
     * A create to make building palettes easier
     */
    public static class Creator {

        private List<GBAColor> colors;

        private Creator(){
            this.colors = new ArrayList<>();
        }

        private Creator(GBAPalette palette){
            this.colors = new ArrayList<>(palette.colors);
        }

        /**
         * Add a color to this palette.
         * @param color The color to add.
         * @return this instance
         * @throws NullPointerException color is null.
         */
        public Creator with(GBAColor color){
            Objects.requireNonNull(color);
            colors.add(color);
            return this;
        }

        /**
         * Add a palette to this one.
         * @param palette The palette to add.
         * @return This instance
         * @throws NullPointerException palette is null.
         */
        public Creator with(GBAPalette palette){
            Objects.requireNonNull(palette);
            colors.addAll(palette.colors);
            return this;
        }

        /**
         * Add a color to this palette.
         * @param color The color to add
         * @return This instance
         * @throws NullPointerException color is null.
         */
        public Creator with(Color color){
            Objects.requireNonNull(color);
            colors.add(GBAColor.from(color));
            return this;
        }

        /**
         * Add a color to this palette.
         * @param r Red value, 0-31
         * @param g Green value, 0-31
         * @param b Blue value, 0-31
         * @return This instance
         * @throws IllegalArgumentException r, g, or b is not between 0 and 31
         */
        public Creator with(int r, int g, int b){
            colors.add(GBAColor.from(r, g, b));
            return this;
        }

        /**
         * Assemble the palette.
         * @return A new palette
         */
        public GBAPalette build(){
            if(colors.isEmpty()){
                throw new IllegalArgumentException("Palette is empty");
            }
            return new GBAPalette(colors);
        }

        /**
         * Pad out the remainder of the palette
         * @param padSize The size the final palette should be.
         * @param paddedColor The color to use for padding.
         * @return A new palette
         * @throws NullPointerException paddedColor is null.
         * @throws IllegalArgumentException padSize is nonpositive.
         */
        public GBAPalette buildToSize(int padSize, GBAColor paddedColor){
            Objects.requireNonNull(paddedColor);
            if(padSize <= 0){
                throw new IllegalArgumentException("padSize must be > 0");
            }
            if(colors.size() < padSize){
                Stream.iterate(colors.size(), i -> i+1).limit(padSize).forEach(i -> colors.add(paddedColor));
            } else if(colors.size() > padSize){
                colors = colors.subList(0, padSize);
            }
            return new GBAPalette(colors);
        }
    }
}
