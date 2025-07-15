package com.manus.kafka.j2ee.config;

import java.util.Map;

/**
 * Interface for loading configuration properties from different sources.
 */
public interface ConfigurationSource {
    
    /**
     * Gets the configuration properties.
     *
     * @return the configuration properties as a Map
     */
    Map<String, Object> getProperties();
    
    /**
     * Gets a specific property value.
     *
     * @param key the property key
     * @return the property value, or null if not found
     */
    default Object getProperty(String key) {
        return getProperties().get(key);
    }
    
    /**
     * Gets a specific property value as a String.
     *
     * @param key the property key
     * @return the property value as a String, or null if not found
     */
    default String getPropertyAsString(String key) {
        Object value = getProperty(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Gets a specific property value as an Integer.
     *
     * @param key the property key
     * @return the property value as an Integer, or null if not found or not a valid Integer
     */
    default Integer getPropertyAsInteger(String key) {
        String value = getPropertyAsString(key);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Gets a specific property value as a Long.
     *
     * @param key the property key
     * @return the property value as a Long, or null if not found or not a valid Long
     */
    default Long getPropertyAsLong(String key) {
        String value = getPropertyAsString(key);
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Gets a specific property value as a Boolean.
     *
     * @param key the property key
     * @return the property value as a Boolean, or null if not found
     */
    default Boolean getPropertyAsBoolean(String key) {
        String value = getPropertyAsString(key);
        if (value == null) {
            return null;
        }
        return Boolean.parseBoolean(value);
    }
}