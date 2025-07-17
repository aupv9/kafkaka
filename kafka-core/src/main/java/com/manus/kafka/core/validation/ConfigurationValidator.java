package com.manus.kafka.core.validation;

import java.util.Map;

/**
 * Interface for validating Kafka configuration properties.
 * Provides comprehensive validation methods that return detailed
 * validation results instead of throwing exceptions.
 */
public interface ConfigurationValidator {
    
    /**
     * Validates the entire configuration and returns a comprehensive result.
     *
     * @param properties the configuration properties to validate
     * @return a ValidationResult containing any errors and warnings
     */
    ValidationResult validate(Map<String, Object> properties);
    
    /**
     * Validates that required properties are present and not null/empty.
     *
     * @param properties the configuration properties to validate
     * @param requiredProperties the names of required properties
     * @return a ValidationResult containing any missing property errors
     */
    ValidationResult validateRequiredProperties(Map<String, Object> properties, String... requiredProperties);
    
    /**
     * Validates that a property value is one of the allowed values.
     *
     * @param properties the configuration properties to validate
     * @param propertyKey the property key to validate
     * @param allowedValues the allowed values for the property
     * @return a ValidationResult containing any invalid value errors
     */
    ValidationResult validateAllowedValues(Map<String, Object> properties, String propertyKey, String... allowedValues);
    
    /**
     * Validates that a numeric property value is within the specified range.
     *
     * @param properties the configuration properties to validate
     * @param propertyKey the property key to validate
     * @param min the minimum allowed value (inclusive)
     * @param max the maximum allowed value (inclusive)
     * @return a ValidationResult containing any out-of-range errors
     */
    ValidationResult validateRange(Map<String, Object> properties, String propertyKey, long min, long max);
    
    /**
     * Validates that a property value is a valid class name that can be loaded
     * and is assignable to the expected type.
     *
     * @param properties the configuration properties to validate
     * @param propertyKey the property key to validate
     * @param expectedType the expected type of the class
     * @return a ValidationResult containing any class validation errors
     */
    ValidationResult validateClass(Map<String, Object> properties, String propertyKey, Class<?> expectedType);
    
    /**
     * Validates a property against a custom validation rule.
     *
     * @param properties the configuration properties to validate
     * @param propertyKey the property key to validate
     * @param validator the custom validation function
     * @return a ValidationResult containing any validation errors
     */
    ValidationResult validateCustom(Map<String, Object> properties, String propertyKey, PropertyValidator validator);
    
    /**
     * Checks for potential configuration issues and returns warnings.
     * This method identifies configurations that are valid but may lead
     * to performance, security, or reliability issues.
     *
     * @param properties the configuration properties to check
     * @return a ValidationResult containing any warnings
     */
    ValidationResult checkForWarnings(Map<String, Object> properties);
    
    /**
     * Validates properties specific to Kafka producers.
     *
     * @param properties the producer configuration properties
     * @return a ValidationResult containing any producer-specific errors and warnings
     */
    ValidationResult validateProducerProperties(Map<String, Object> properties);
    
    /**
     * Validates properties specific to Kafka consumers.
     *
     * @param properties the consumer configuration properties
     * @return a ValidationResult containing any consumer-specific errors and warnings
     */
    ValidationResult validateConsumerProperties(Map<String, Object> properties);
    
    /**
     * Validates properties specific to Kafka admin clients.
     *
     * @param properties the admin client configuration properties
     * @return a ValidationResult containing any admin client-specific errors and warnings
     */
    ValidationResult validateAdminClientProperties(Map<String, Object> properties);
    
    /**
     * Functional interface for custom property validation.
     */
    @FunctionalInterface
    interface PropertyValidator {
        /**
         * Validates a property value and returns a validation result.
         *
         * @param propertyName the name of the property being validated
         * @param value the value to validate
         * @return a ValidationResult containing any errors or warnings
         */
        ValidationResult validate(String propertyName, Object value);
    }
}