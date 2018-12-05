package com.github.lucbui.bytes;

import java.nio.ByteBuffer;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToLongFunction;
import org.apache.commons.lang3.NotImplementedException;

public class ByteWindow {

    public static byte DEFAULT_VALUE = 0;
    private byte defaultValue;
    private Map<Long, Byte> bytes;

    public ByteWindow() {
        this(DEFAULT_VALUE);
    }

    public ByteWindow(ByteBuffer bb) {
        this(DEFAULT_VALUE);
        this.set(0, bb);
    }

    public ByteWindow(byte defaultValue) {
        this.bytes = new TreeMap<>();
        this.defaultValue = defaultValue;
    }

    public byte get(long position) {
        return this.bytes.getOrDefault(position, this.defaultValue);
    }

    public byte[] get(long position, int number) {
        byte[] bites = new byte[number];

        for(int idx = 0; idx < number; ++idx) {
            bites[idx] = this.get(position + (long)idx);
        }

        return bites;
    }

    public void set(long position, byte val) {
        this.bytes.put(position, val);
    }

    public void set(long position, byte[] val) {
        for(int idx = 0; idx < val.length; ++idx) {
            this.set(position + (long)idx, val[idx]);
        }

    }

    public void set(long position, ByteBuffer biteBuffer) {
        if (biteBuffer.hasArray()) {
            this.set(position, biteBuffer.array());
        } else {
            throw new NotImplementedException("Can't handle byteBuffers not backed by arrays");
        }
    }

    public long getRange() {
        LongSummaryStatistics stats = this.bytes.keySet().stream().mapToLong(k -> k).summaryStatistics();
        return stats.getMax() - stats.getMin();
    }

    public int getNumberOfBytes() {
        return this.bytes.size();
    }

    public boolean isComplete() {
        return this.getRange() == (long)this.getNumberOfBytes();
    }

    public ByteWindow subWindow(long fromPtr, long toPtr) {
        ByteWindow window = new ByteWindow();
        window.bytes = new TreeMap<>();
        this.bytes.keySet().stream().filter(k -> k >= fromPtr && k < toPtr)
                .forEach(k -> window.bytes.put(k, this.bytes.get(k)));
        return window;
    }

    public void forEach(BiConsumer<? super Long, ? super Byte> consumer) {
        this.bytes.forEach(consumer);
    }
}
