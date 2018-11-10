package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

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
    public GBAPalette(List<GBAColor> colors){
        Objects.requireNonNull(colors).forEach(Objects::requireNonNull);
        if(colors.isEmpty()){
            throw new IllegalArgumentException("GBAPaletteConfig supplied must have size > 0");
        }
        this.colors = new ArrayList<>(colors);
    }

    /**
     * Copy a GBAPalette from one.
     * @param palette The palette to copy.
     */
    public GBAPalette(GBAPalette palette){
        Objects.requireNonNull(palette);
        this.colors = new ArrayList<>(palette.colors);
    }

    /**
     * Get the colors in this palette.
     * The returned collection is an immutable list containing all the colors in this palette.
     * @return The colors in this palette.
     */
    public List<GBAColor> getColors(){
        return Collections.unmodifiableList(this.colors);
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

    public GBAPalette shiftPalette(int shift){
        List<GBAColor> newColors = new ArrayList<>(this.colors);
        Collections.rotate(newColors, shift);
        return new GBAPalette(newColors);
    }

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
     * Get a hex reader to read a palette.
     * @param numberOfColors The number of colors to read.
     * @return
     */
    public static HexReader<GBAPalette> getHexReader(int numberOfColors){
        return iterator -> {
            List<GBAColor> colors = new ArrayList<>();
            for(int count = 0; count < numberOfColors; count++){
                colors.add(GBAColor.HEX_READER.read(iterator));
                iterator.advanceRelative(2);
            }
            return new GBAPalette(colors);
        };
    }

    /**
     * Get a hex writer to write a palette.
     * @param numberOfColors The number of colors to read.
     * @return
     */
    public static HexWriter<GBAPalette> getHexWriter(int numberOfColors){
        return (object, iterator) -> {
            for(int idx = 0; idx < numberOfColors; idx++){
                GBAColor color = object.get(idx);
                GBAColor.HEX_WRITER.write(color, iterator);
                iterator.advanceRelative(2);
            }
        };
    }

    /**
     * Create a PaletteBuilder to easily create a palette.
     * @return
     */
    public static Builder builder(){
        return new Builder();
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
     * A builder to make building palettes easier
     */
    public static class Builder{

        private List<GBAColor> colors;

        private Builder(){
            this.colors = new ArrayList<>();
        }

        /**
         * Add a color to this palette.
         * @param color The color to add.
         * @return
         */
        public Builder with(GBAColor color){
            colors.add(color);
            return this;
        }

        /**
         * Add a palette to this one.
         * @param palette The palette to add.
         * @return
         */
        public Builder with(GBAPalette palette){
            colors.addAll(palette.colors);
            return this;
        }

        /**
         * Add a color to this palette.
         * @param color The color to add
         * @return
         */
        public Builder with(Color color){
            colors.add(GBAColor.from(color));
            return this;
        }

        /**
         * Add a color to this palette.
         * @param r Red value, 0-31
         * @param g Green value, 0-31
         * @param b Blue value, 0-31
         * @return
         */
        public Builder with(int r, int g, int b){
            colors.add(GBAColor.from(r, g, b));
            return this;
        }

        /**
         * Assemble the palette.
         * @return
         */
        public GBAPalette build(){
            return new GBAPalette(colors);
        }
    }
}
