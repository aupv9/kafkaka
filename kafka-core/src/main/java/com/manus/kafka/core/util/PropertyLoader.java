package com.manus.kafka.core.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class for loading properties from different sources.
 */
public class PropertyLoader {
    
    /**
     * Loads properties from a file.
     *
     * @param filePath the path to the properties file
     * @return the loaded properties as a Map
     * @throws IOException if an I/O error occurs
     */
    public static Map<String, Object> loadFromFile(String filePath) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            properties.load(inputStream);
        }
        return propertiesToMap(properties);
    }
    
    /**
     * Loads properties from a classpath resource.
     *
     * @param resourcePath the path to the resource
     * @return the loaded properties as a Map
     * @throws IOException if an I/O error occurs
     */
    public static Map<String, Object> loadFromClasspath(String resourcePath) throws IOException {
        Properties properties = new Properties();
        try (InputStream inputStream = PropertyLoader.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }
            properties.load(inputStream);
        }
        return propertiesToMap(properties);
    }
    
    /**
     * Loads properties from environment variables with a specific prefix.
     * For example, if prefix is "KAFKA_", then environment variables like "KAFKA_BOOTSTRAP_SERVERS"
     * will be loaded as "bootstrap.servers".
     *
     * @param prefix the prefix of environment variables to load
     * @return the loaded properties as a Map
     */
    public static Map<String, Object> loadFromEnvironment(String prefix) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> env = System.getenv();
        
        for (Map.Entry<String, String> entry : env.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(prefix)) {
                // Convert from environment variable format to Kafka property format
                // e.g., KAFKA_BOOTSTRAP_SERVERS -> bootstrap.servers
                String propertyKey = key.substring(prefix.length())
                        .toLowerCase()
                        .replace('_', '.');
                result.put(propertyKey, entry.getValue());
            }
        }
        
        return result;
    }
    
    /**
     * Loads properties from system properties with a specific prefix.
     * For example, if prefix is "kafka.", then system properties like "kafka.bootstrap.servers"
     * will be loaded as "bootstrap.servers".
     *
     * @param prefix the prefix of system properties to load
     * @return the loaded properties as a Map
     */
    public static Map<String, Object> loadFromSystemProperties(String prefix) {
        Map<String, Object> result = new HashMap<>();
        Properties systemProperties = System.getProperties();
        
        for (String key : systemProperties.stringPropertyNames()) {
            if (key.startsWith(prefix)) {
                // Remove the prefix
                String propertyKey = key.substring(prefix.length());
                result.put(propertyKey, systemProperties.getProperty(key));
            }
        }
        
        return result;
    }
    
    /**
     * Merges multiple property maps, with later maps overriding earlier ones.
     *
     * @param maps the property maps to merge
     * @return the merged properties as a Map
     */
    @SafeVarargs
    public static Map<String, Object> merge(Map<String, Object>... maps) {
        Map<String, Object> result = new HashMap<>();
        
        for (Map<String, Object> map : maps) {
            result.putAll(map);
        }
        
        return result;
    }
    
    /**
     * Converts a Properties object to a Map.
     *
     * @param properties the Properties object to convert
     * @return the properties as a Map
     */
    private static Map<String, Object> propertiesToMap(Properties properties) {
        Map<String, Object> result = new HashMap<>();
        
        for (String key : properties.stringPropertyNames()) {
            result.put(key, properties.getProperty(key));
        }
        
        return result;
    }
}