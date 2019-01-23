package com.github.lucbui.config;

import com.github.lucbui.utility.Try;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
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
    public static Try<PropertiesConfig> fromFile(String filename){
        Properties properties = new Properties();
        try(InputStream in = new FileInputStream(filename)) {
            properties.load(in);
            return Try.ok(new PropertiesConfig(properties));
        } catch (Exception ex){
            return Try.error(ex);
        }
    }

    /**
     * Initialize a PropertiesConfig from an XML properties file.
     * @param filename The file to parse
     * @throws IOException
     */
    public static Try<PropertiesConfig> fromXml(String filename) {
        Properties properties = new Properties();
        try(InputStream in = new FileInputStream(filename)) {
            properties.loadFromXML(in);
            return Try.ok(new PropertiesConfig(properties));
        } catch (Exception ex){
            return Try.error(ex);
        }
    }

    @Override
    public Optional<String> get(String key) {
        return Optional.ofNullable(properties.getProperty(key));
    }

    @Override
    public boolean has(String key) {
        return properties.containsKey(key);
    }
}
