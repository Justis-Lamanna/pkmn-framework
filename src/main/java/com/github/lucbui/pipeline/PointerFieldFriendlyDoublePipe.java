package com.github.lucbui.pipeline;

import com.github.lucbui.framework.FieldObject;

/**
 * A DoublePipe that correctly handles PointerFields
 * When reading, if the incoming field has the @PointerField annotation, a Pointer is read at the iterator, rather than
 * the normal object. The actual object is then read from that Pointer.
 *
 * If @PointerField is not encountered, the object is read as normal.
 *
 * When writing, If the incoming field has the @PointerField annotation, the repoint strategy associated with the PointerObject
 * is run. The associated pointer is written, the FieldObject's referent is changed to be the PointerObject's object,
 * and the FieldObject's pointer is changed to the new pointer.
 */
public interface PointerFieldFriendlyDoublePipe extends DoublePipe<FieldObject>, PointerFieldFriendlyReadPipe, PointerFieldFriendlyWritePipe {
}
