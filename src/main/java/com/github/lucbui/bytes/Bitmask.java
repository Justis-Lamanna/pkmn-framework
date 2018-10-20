package com.github.lucbui.bytes;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a Bitmask operation
 */
public class Bitmask {

    private int mask;
    private int rightShift;

    /**
     * A Bitmask operation which masks and right-shifts.
     * @param mask The mask to use.
     * @param rightShift The shift amount to use.
     */
    public Bitmask(int mask, int rightShift){
        this.mask = mask;
        this.rightShift = rightShift;
    }

    /**
     * A Bitmask operation which only masks.
     * @param mask The mask to use.
     */
    public Bitmask(int mask){
        this.mask = mask;
        this.rightShift = 0;
    }

    /**
     * Get the mask applied to fields
     * @return
     */
    public int getMask() {
        return mask;
    }

    /**
     * Get the right-shift applied to fields
     * @return
     */
    public int getRightShift() {
        return rightShift;
    }

    /**
     * Apply a Bitmask to an integer.
     * @param value The value to modify.
     * @return The modified value.
     */
    public int apply(int value){
        return (value & mask) >>> rightShift;
    }

    /**
     * Create one value from several using bitmasks.
     * @param mergeStart The value to start with.
     * @return The BitmaskMerge object to begin using.
     */
    public static BitmaskMerge merge(int mergeStart){
        return new BitmaskMerge(mergeStart);
    }

    /**
     * Use this Bitmask to apply one value to another.
     * @return The merged value.
     */
    public static BitmaskMerge merge(){
        return new BitmaskMerge(0);
    }

    @Override
    public String toString() {
        return "Bitmask{" +
                "mask=0b" + Integer.toBinaryString(mask) +
                ", rightShift=" + rightShift +
                '}';
    }

    /**
     * A merge operation using bitmasks to combine several values into one.
     */
    public static class BitmaskMerge{

        private int mergeStart;

        private List<Bitmask> masksToApply;
        private List<Integer> numbersToApply;

        /**
         * Initialize a BitmaskMerge
         * @param mergeStart The value to intitialize with.
         */
        private BitmaskMerge(int mergeStart){
            this.mergeStart = mergeStart;
            this.masksToApply = new ArrayList<>();
            this.numbersToApply = new ArrayList<>();
        }

        /**
         * Add a mask and value to merge.
         * @param mask The mask to use.
         * @param value The value to combine with
         * @return This instance with chaining.
         */
        public BitmaskMerge with(Bitmask mask, int value){
            masksToApply.add(mask);
            numbersToApply.add(value);
            return this;
        }

        /**
         * Apply the results of the merge.
         * @return The merge results.
         */
        public int apply(){
            int value = mergeStart;
            for(int idx = 0; idx < masksToApply.size(); idx++){
                value = value | ((numbersToApply.get(idx) << masksToApply.get(idx).rightShift) & masksToApply.get(idx).mask);
            }
            return value;
        }
    }
}
