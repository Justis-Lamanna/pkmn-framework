package com.github.lucbui.framework;

import com.github.lucbui.bytes.HexWriter;
import com.github.lucbui.bytes.Hexer;
import com.github.lucbui.bytes.PointerObject;
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
     * Combines a Hexer with a RepointStrategy
     * @param hexer The Hexer to use.
     * @param repointStrategy The repoint strategy to use.
     * @param <T> The type in the HexWriter
     * @return
     */
    public static <T> HexWriter<PointerObject<T>> writeWithRepoint(Hexer<T> hexer, RepointStrategy repointStrategy){
        return (object, iterator) -> {
            RepointMetadata repointMetadata = new RepointMetadata(object);
            Pointer ptr = repointStrategy.repoint(repointMetadata);
            hexer.write(object.getObject(), iterator.copy(ptr.getLocation()));
        };
    }
}
