package com.github.lucbui.utility;

/**
 * A specialized form of Supplier that can throw an exception
 * @param <T> The type to retrieve from the exception
 */
@FunctionalInterface
public interface SupplierWithException<T>{
    T get() throws Exception;
}
