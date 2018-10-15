package com.github.lucbui.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * An immutable configuration that is backed by a Properties object.
 */
public class PropertiesConfig implements Configuration{

    private Properties properties;

    /**
     * Initialize a PropertiesConfig from a pre-existing Properties object.
     * @param properties The properties to use.
     */
    public PropertiesConfig(Properties properties){
        this.properties = new Properties(properties);
    }

    /**
     * Initialize a PropertiesConfig from a properties file.
     * @param filename The file to parse
     * @throws IOException
     */
    public static PropertiesConfig fromFile(String filename) throws IOException {
        Properties properties = new Properties();
        try(InputStream in = new FileInputStream(filename)) {
            properties.load(in);
            return new PropertiesConfig(properties);
        }
    }

    /**
     * Initialize a PropertiesConfig from an XML properties file.
     * @param filename The file to parse
     * @throws IOException
     */
    public static PropertiesConfig fromXml(String filename) throws IOException {
        Properties properties = new Properties();
        try(InputStream in = new FileInputStream(filename)) {
            properties.loadFromXML(in);
            return new PropertiesConfig(properties);
        }
    }

    @Override
    public String get(String key) {
        return properties.getProperty(key);
    }

    @Override
    public boolean has(String key) {
        return properties.containsKey(key);
    }
}
