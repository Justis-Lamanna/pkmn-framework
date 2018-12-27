package com.github.lucbui.utility;

import com.github.lucbui.annotations.DataStructure;
import com.github.lucbui.bytes.Hexer;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HexerUtils {
    /**
     *
     * @param hexers
     * @param type
     * @param <T>
     * @return
     */
    public static <T> Optional<Hexer<T>> getHexerFor(Map<Class<?>, Hexer<?>> hexers, Class<T> type) {
        Hexer<T> reader = findHexerInSubclasses(hexers, type);
        return Optional.ofNullable(reader);
    }

    @SuppressWarnings("unchecked")
    private static <T> Hexer<T> findHexerInSubclasses(Map<Class<?>, Hexer<?>> hexers, Class<T> type) {
        if(hexers.containsKey(type)){
            return (Hexer<T>) hexers.get(type);
        }
        Map<Class<?>, Hexer<?>> readers = hexers.keySet().stream()
                .filter(type::isAssignableFrom)
                .collect(Collectors.toMap(Function.identity(), hexers::get));
        if(readers.isEmpty()){
            return null;
        } else if(readers.size() > 1){
            throw new IllegalArgumentException("Error finding reader: " + type + " matches: " + readers.keySet() + ". Please disambiguate the reader.");
        } else {
            return (Hexer<T>) readers.values().stream().findFirst().orElseThrow(RuntimeException::new);
        }
    }
}
