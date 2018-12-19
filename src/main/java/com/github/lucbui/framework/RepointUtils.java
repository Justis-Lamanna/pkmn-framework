package com.github.lucbui.framework;

import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.bytes.PointerObject;
import com.github.lucbui.file.HexFieldIterator;
import com.github.lucbui.file.Pointer;
import org.apache.commons.lang3.NotImplementedException;

public class RepointUtils {

    private RepointUtils(){
        //
    }

    /**
     * A repoint strategy where no repointing should occur.
     * If a repoint attempts to occur with this strategy, an IllegalStateException is thrown.
     */
    public static RepointStrategy disableRepointStrategy(){
        return new NoRepointStrategy();
    }

    /**
     * A repoint strategy where the object is not repointed, and stored in the same place.
     */
    public static RepointStrategy identityRepointStrategy(){
        return metadata -> metadata.getPointerObject().getPointer();
    }

    /**
     * A strategy where the object is repointed to wherever there is free space.
     * Not Implemented Yet!!
     * @return
     */
    public static RepointStrategy findFreeSpaceRepointStrategy(byte freeSpaceByte){
        throw new NotImplementedException("TODO");
    }

    /**
     * Combines a HexWriter with a RepointStrategy
     * @param hexWriter The HexWriter to use.
     * @param repointStrategy The repoint strategy to use.
     * @param <T> The type in the HexWriter
     * @return
     */
    public static <T> HexWriter<PointerObject<T>> writeWithRepoint(HexWriter<T> hexWriter, RepointStrategy repointStrategy){
        return (object, iterator) -> {
            RepointStrategy.RepointMetadata repointMetadata = new RepointStrategy.RepointMetadata(object, -1);
            Pointer ptr = repointStrategy.repoint(repointMetadata);
            hexWriter.write(object.getObject(), iterator.copy(ptr.getLocation()));
        };
    }
}
