package com.manus.kafka.core.util;

import com.manus.kafka.core.validation.DefaultConfigurationValidator;
import com.manus.kafka.core.validation.ValidationResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for validating Kafka configurations.
 * 
 * @deprecated Use {@link com.manus.kafka.core.validation.ConfigurationValidator} interface
 * and {@link DefaultConfigurationValidator} implementation instead.
 */
@Deprecated
public class ConfigurationValidator {
    
    private static final com.manus.kafka.core.validation.ConfigurationValidator VALIDATOR = 
        new DefaultConfigurationValidator();
    
    /**
     * Validates that required properties are present in the configuration.
     *
     * @param properties the properties to validate
     * @param requiredProperties the required property keys
     * @throws IllegalStateException if any required property is missing
     */
    public static void validateRequiredProperties(Map<String, Object> properties, String... requiredProperties) {
        ValidationResult result = VALIDATOR.validateRequiredProperties(properties, requiredProperties);
        if (!result.isValid()) {
            List<String> missingProperties = new ArrayList<>();
            result.getErrors().forEach(error -> missingProperties.add(error.getPropertyName()));
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
        ValidationResult result = VALIDATOR.validateAllowedValues(properties, propertyKey, allowedValues);
        if (!result.isValid()) {
            throw new IllegalStateException(result.getErrors().get(0).getMessage());
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
        ValidationResult result = VALIDATOR.validateRange(properties, propertyKey, min, max);
        if (!result.isValid()) {
            throw new IllegalStateException(result.getErrors().get(0).getMessage());
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
        ValidationResult result = VALIDATOR.validateClass(properties, propertyKey, expectedType);
        if (!result.isValid()) {
            throw new IllegalStateException(result.getErrors().get(0).getMessage());
        }
    }
    
    /**
     * Warns about potential issues with the configuration.
     *
     * @param properties the properties to check
     * @return a list of warning messages
     */
    public static List<String> checkForWarnings(Map<String, Object> properties) {
        ValidationResult result = VALIDATOR.checkForWarnings(properties);
        List<String> warnings = new ArrayList<>();
        result.getWarnings().forEach(warning -> warnings.add(warning.getMessage()));
        return warnings;
    }
}