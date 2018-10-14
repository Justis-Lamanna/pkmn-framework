package com.github.lucbui.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A basic configuration which is based on a HashMap
 */
public class MapConfiguration implements Configuration {

    private Map<String, String> internalMap;

    /**
     * Initializes a MapConfiguration with a map.
     * The map is copied, and made unmodifiable, before being used in this configuration.
     * @param map
     */
    public MapConfiguration(Map<String, String> map){
        this.internalMap = Collections.unmodifiableMap(new HashMap<>(map));
    }

    /**
     * Initializes a MapConfiguration with pairs of strings.
     * @param pairs A list of strings, alternating between key and value.
     */
    public MapConfiguration(String... pairs){
        if(pairs.length % 2 != 0){
            throw new IllegalArgumentException("Odd number of pairs specified.");
        }
        Map<String, String> seedMap = new HashMap<>();
        for(int idx = 0; idx < pairs.length; idx += 2){
            seedMap.put(pairs[idx], pairs[idx + 1]);
        }
        internalMap = Collections.unmodifiableMap(seedMap);
    }

    @Override
    public String get(String key) {
        return internalMap.get(key);
    }

    @Override
    public boolean has(String key) {
        return internalMap.containsKey(key);
    }

    @Override
    public String toString() {
        return "MapConfiguration{" +
                "internalMap=" + internalMap +
                '}';
    }
}
