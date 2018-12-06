package com.github.lucbui.bytes;

/**
 * A common interface to all byte objects.
 * @param <T> The subobject.
 */
public interface ByteObject<T> {

    /**
     * Get the value of a ByteObject.
     * @return The byte's value, as a long.
     */
    long getValue();

    /**
     * Create a new instance of this object with a new value.
     * @param newValue The new value.
     * @return The new instance of the object.
     */
    T newInstance(long newValue);

    /**
     * Adds two ByteObjects together.
     * @param other The other ByteObject to use.
     * @return A new object that is the sum of this and the other.
     */
    default T add(ByteObject<?> other){
        return newInstance(this.getValue() + other.getValue());
    }

    /**
     * Add some amount to this ByteObject.
     * @param other The other value to use.
     * @return A new object that is the sum of this and other.
     */
    default T add(long other){
        return newInstance(this.getValue() + other);
    }

    /**
     * Subtract two ByteObjects.
     * @param other The other ByteObject to use.
     * @return A new object that is the difference of this and the other.
     */
    default T subtract(ByteObject<?> other){
        return newInstance(this.getValue() - other.getValue());
    }

    /**
     * Subtract some amount from this ByteObject.
     * @param other The other value to use.
     * @return A new object that is the difference of this and other.
     */
    default T subtract(long other){
        return newInstance(this.getValue() - other);
    }
}
