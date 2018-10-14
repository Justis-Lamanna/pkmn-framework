package com.github.lucbui.config;

import com.sun.scenario.effect.Merge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A configuration which allows several different configurations to be used in tandem.
 * A list of configurations is stored in memory, which can "overlap" one-another. When searching
 * for a value, the first configuration is searched, followed by the second, and so on. The first
 * value found is returned.
 */
public class MergedConfiguration implements Configuration{

    private List<Configuration> configurationList;

    /**
     * Initialize a MergedConfiguration.
     * @param configurations A list of configurations.
     */
    public MergedConfiguration(List<Configuration> configurations){
        this.configurationList = new ArrayList<>(configurations);
    }

    /**
     * Initialize a Merged Configuration.
     * @param configurations A list of configurations.
     */
    public MergedConfiguration(Configuration... configurations){
        this.configurationList = Arrays.asList(configurations);
    }

    @Override
    public String get(String key) {
        return configurationList.stream()
                .filter(i -> i.has(key))
                .map(i -> i.get(key))
                .findFirst().orElse(null);
    }

    @Override
    public boolean has(String key) {
        return configurationList.stream()
                .anyMatch(conf -> conf.has(key));
    }
}
