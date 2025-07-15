package com.manus.kafka.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for validating Kafka configurations.
 */
public class ConfigurationValidator {
    
    /**
     * Validates that required properties are present in the configuration.
     *
     * @param properties the properties to validate
     * @param requiredProperties the required property keys
     * @throws IllegalStateException if any required property is missing
     */
    public static void validateRequiredProperties(Map<String, Object> properties, String... requiredProperties) {
        List<String> missingProperties = new ArrayList<>();
        
        for (String requiredProperty : requiredProperties) {
            if (!properties.containsKey(requiredProperty) || properties.get(requiredProperty) == null) {
                missingProperties.add(requiredProperty);
            }
        }
        
        if (!missingProperties.isEmpty()) {
            throw new IllegalStateException("Missing required properties: " + String.join(", ", missingProperties));
        }
    }
    
    /**
     * Validates that a property value is one of the allowed values.
     *
     * @param properties the properties to validate
     * @param propertyKey the property key to validate
     * @param allowedValues the allowed values for the property
     * @throws IllegalStateException if the property value is not one of the allowed values
     */
    public static void validateAllowedValues(Map<String, Object> properties, String propertyKey, String... allowedValues) {
        if (!properties.containsKey(propertyKey)) {
            return; // Property not set, nothing to validate
        }
        
        Object value = properties.get(propertyKey);
        if (value == null) {
            return; // Property is null, nothing to validate
        }
        
        String stringValue = value.toString();
        boolean valid = false;
        
        for (String allowedValue : allowedValues) {
            if (allowedValue.equals(stringValue)) {
                valid = true;
                break;
            }
        }
        
        if (!valid) {
            throw new IllegalStateException("Invalid value for property " + propertyKey + ": " + stringValue + 
                    ". Allowed values are: " + String.join(", ", allowedValues));
        }
    }
    
    /**
     * Validates that a numeric property value is within a range.
     *
     * @param properties the properties to validate
     * @param propertyKey the property key to validate
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @throws IllegalStateException if the property value is not within the range
     */
    public static void validateRange(Map<String, Object> properties, String propertyKey, long min, long max) {
        if (!properties.containsKey(propertyKey)) {
            return; // Property not set, nothing to validate
        }
        
        Object value = properties.get(propertyKey);
        if (value == null) {
            return; // Property is null, nothing to validate
        }
        
        long longValue;
        try {
            if (value instanceof Number) {
                longValue = ((Number) value).longValue();
            } else {
                longValue = Long.parseLong(value.toString());
            }
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Invalid numeric value for property " + propertyKey + ": " + value);
        }
        
        if (longValue < min || longValue > max) {
            throw new IllegalStateException("Value for property " + propertyKey + " must be between " + min + " and " + max + 
                    ", but was: " + longValue);
        }
    }
    
    /**
     * Validates that a property value is a valid class name and can be loaded.
     *
     * @param properties the properties to validate
     * @param propertyKey the property key to validate
     * @param expectedType the expected type of the class
     * @throws IllegalStateException if the property value is not a valid class name or cannot be loaded
     */
    public static void validateClass(Map<String, Object> properties, String propertyKey, Class<?> expectedType) {
        if (!properties.containsKey(propertyKey)) {
            return; // Property not set, nothing to validate
        }
        
        Object value = properties.get(propertyKey);
        if (value == null) {
            return; // Property is null, nothing to validate
        }
        
        String className = value.toString();
        try {
            Class<?> clazz = Class.forName(className);
            if (!expectedType.isAssignableFrom(clazz)) {
                throw new IllegalStateException("Class " + className + " for property " + propertyKey + 
                        " does not implement or extend " + expectedType.getName());
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class not found for property " + propertyKey + ": " + className);
        }
    }
    
    /**
     * Warns about potential issues with the configuration.
     *
     * @param properties the properties to check
     * @return a list of warning messages
     */
    public static List<String> checkForWarnings(Map<String, Object> properties) {
        List<String> warnings = new ArrayList<>();
        
        // Check for bootstrap servers
        if (properties.containsKey("bootstrap.servers")) {
            String bootstrapServers = properties.get("bootstrap.servers").toString();
            if (!bootstrapServers.contains(",")) {
                warnings.add("Only one bootstrap server is configured. This is not recommended for production environments.");
            }
        }
        
        // Check for acks=0 in producer
        if ("0".equals(properties.get("acks"))) {
            warnings.add("acks=0 provides no guarantee that records have been received by the broker. This may result in data loss.");
        }
        
        // Check for retries=0 in producer
        if (Integer.valueOf(0).equals(properties.get("retries"))) {
            warnings.add("retries=0 means no retries will be performed. This may result in message loss on transient errors.");
        }
        
        // Check for enable.auto.commit=true in consumer
        if ("true".equals(properties.get("enable.auto.commit"))) {
            warnings.add("enable.auto.commit=true may result in duplicate processing or message loss in case of errors. Consider using manual commit for better control.");
        }
        
        // Check for auto.offset.reset=latest in consumer
        if ("latest".equals(properties.get("auto.offset.reset"))) {
            warnings.add("auto.offset.reset=latest will cause the consumer to miss messages that were sent while it was offline.");
        }
        
        return warnings;
    }
}