package com.github.lucbui.framework;

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
        return metadata -> {
            throw new IllegalStateException("Cannot repoint.");
        };
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
}
