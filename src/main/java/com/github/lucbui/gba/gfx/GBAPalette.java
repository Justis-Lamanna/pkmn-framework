package com.github.lucbui.gba.gfx;

import com.github.lucbui.bytes.HexReader;
import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.file.HexFieldIterator;

import java.util.*;

/**
 * A palette which holds a list of GBAColors
 */
public class GBAPalette {
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

    public List<GBAColor> getAsList(){
        return new ArrayList<>(this.colors);
    }

    /**
     * Get a color in the palette.
     * @param slot The slot to retrieve.
     * @return The color retrieved.
     */
    public GBAColor get(int slot){
        validateSlot(slot);
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
     * Set a color in the palette.
     * @param slot The slot to set.
     * @param color The color to set it to.
     * @return This instance, if you want to set several colors at once.
     */
    public GBAPalette set(int slot, GBAColor color){
        validateSlot(slot);
        Objects.requireNonNull(color);
        colors.set(slot, color);
        return this;
    }

    /**
     * Tests if a palette has a color at the specified slot.
     * @param slot The slot to view.
     * @return True if the palette has a color registered in that slot.
     */
    public boolean hasColor(int slot){
        validateSlot(slot);
        return slot < colors.size();
    }

    /**
     * Get the size of the palette.
     * @return
     */
    public int size(){
        return colors.size();
    }

    private void validateSlot(int slot){
        if(slot < 0){
            throw new IllegalArgumentException("Slot specified < 0");
        }
    }

    @Override
    public String toString() {
        return "GBAPalette{" +
                "colors=" + colors +
                '}';
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

    public static HexWriter<GBAPalette> getHexWriter(int numberOfColors){
        return (object, iterator) -> {
            for(int idx = 0; idx < numberOfColors; idx++){
                GBAColor color = object.get(idx);
                GBAColor.HEX_WRITER.write(color, iterator);
                iterator.advanceRelative(2);
            }
        };
    }

    public static Builder builder(){
        return new Builder();
    }

    /**
     * A builder to make building palettes easier
     */
    public static class Builder{

        private List<GBAColor> colors;

        private Builder(){
            this.colors = new ArrayList<>();
        }

        public Builder with(GBAColor color){
            colors.add(color);
            return this;
        }

        public GBAPalette build(){
            return new GBAPalette(colors);
        }
    }
}
