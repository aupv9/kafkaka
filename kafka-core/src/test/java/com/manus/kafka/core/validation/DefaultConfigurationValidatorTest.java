package com.manus.kafka.core.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DefaultConfigurationValidator}.
 */
public class DefaultConfigurationValidatorTest {

    private DefaultConfigurationValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new DefaultConfigurationValidator();
    }

    @Test
    @DisplayName("Test validate with null properties")
    public void testValidateWithNullProperties() {
        // When
        ValidationResult result = validator.validate(null);
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("Configuration properties cannot be null", result.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Test validate with valid properties")
    public void testValidateWithValidProperties() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092,localhost:9093");
        
        // When
        ValidationResult result = validator.validate(properties);
        
        // Then
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }

    @Test
    @DisplayName("Test validateRequiredProperties with missing properties")
    public void testValidateRequiredPropertiesWithMissingProperties() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        
        // When
        ValidationResult result = validator.validateRequiredProperties(properties, "bootstrap.servers", "group.id");
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("group.id", result.getErrors().get(0).getPropertyName());
        assertEquals(ValidationErrorType.MISSING_REQUIRED, result.getErrors().get(0).getType());
    }

    @Test
    @DisplayName("Test validateRequiredProperties with empty string values")
    public void testValidateRequiredPropertiesWithEmptyStringValues() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "   "); // Empty string
        
        // When
        ValidationResult result = validator.validateRequiredProperties(properties, "bootstrap.servers", "group.id");
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("group.id", result.getErrors().get(0).getPropertyName());
    }

    @Test
    @DisplayName("Test validateAllowedValues with valid value")
    public void testValidateAllowedValuesWithValidValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("acks", "all");
        
        // When
        ValidationResult result = validator.validateAllowedValues(properties, "acks", "0", "1", "all");
        
        // Then
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }

    @Test
    @DisplayName("Test validateAllowedValues with invalid value")
    public void testValidateAllowedValuesWithInvalidValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("acks", "invalid");
        
        // When
        ValidationResult result = validator.validateAllowedValues(properties, "acks", "0", "1", "all");
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("acks", result.getErrors().get(0).getPropertyName());
        assertEquals(ValidationErrorType.INVALID_VALUE, result.getErrors().get(0).getType());
    }

    @Test
    @DisplayName("Test validateRange with valid value")
    public void testValidateRangeWithValidValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("request.timeout.ms", 30000);
        
        // When
        ValidationResult result = validator.validateRange(properties, "request.timeout.ms", 1000, 60000);
        
        // Then
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }

    @Test
    @DisplayName("Test validateRange with out of range value")
    public void testValidateRangeWithOutOfRangeValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("request.timeout.ms", 500);
        
        // When
        ValidationResult result = validator.validateRange(properties, "request.timeout.ms", 1000, 60000);
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("request.timeout.ms", result.getErrors().get(0).getPropertyName());
        assertEquals(ValidationErrorType.OUT_OF_RANGE, result.getErrors().get(0).getType());
    }

    @Test
    @DisplayName("Test validateRange with invalid numeric value")
    public void testValidateRangeWithInvalidNumericValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("request.timeout.ms", "not-a-number");
        
        // When
        ValidationResult result = validator.validateRange(properties, "request.timeout.ms", 1000, 60000);
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("request.timeout.ms", result.getErrors().get(0).getPropertyName());
        assertEquals(ValidationErrorType.INVALID_FORMAT, result.getErrors().get(0).getType());
    }

    @Test
    @DisplayName("Test validateClass with valid class")
    public void testValidateClassWithValidClass() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        // When
        ValidationResult result = validator.validateClass(properties, "key.serializer", 
            org.apache.kafka.common.serialization.Serializer.class);
        
        // Then
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }

    @Test
    @DisplayName("Test validateClass with invalid class")
    public void testValidateClassWithInvalidClass() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("key.serializer", "java.lang.String");
        
        // When
        ValidationResult result = validator.validateClass(properties, "key.serializer", 
            org.apache.kafka.common.serialization.Serializer.class);
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("key.serializer", result.getErrors().get(0).getPropertyName());
        assertEquals(ValidationErrorType.INVALID_CLASS, result.getErrors().get(0).getType());
    }

    @Test
    @DisplayName("Test validateClass with non-existent class")
    public void testValidateClassWithNonExistentClass() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("key.serializer", "com.nonexistent.Class");
        
        // When
        ValidationResult result = validator.validateClass(properties, "key.serializer", 
            org.apache.kafka.common.serialization.Serializer.class);
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("key.serializer", result.getErrors().get(0).getPropertyName());
        assertEquals(ValidationErrorType.INVALID_CLASS, result.getErrors().get(0).getType());
    }

    @Test
    @DisplayName("Test checkForWarnings with single bootstrap server")
    public void testCheckForWarningsWithSingleBootstrapServer() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        
        // When
        ValidationResult result = validator.checkForWarnings(properties);
        
        // Then
        assertTrue(result.isValid()); // Warnings don't make result invalid
        assertTrue(result.hasWarnings());
        assertEquals(1, result.getWarnings().size());
        assertEquals("bootstrap.servers", result.getWarnings().get(0).getPropertyName());
        assertEquals(ValidationWarningType.PERFORMANCE, result.getWarnings().get(0).getType());
    }

    @Test
    @DisplayName("Test validateProducerProperties with valid configuration")
    public void testValidateProducerPropertiesWithValidConfiguration() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092,localhost:9093");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("acks", "all");
        
        // When
        ValidationResult result = validator.validateProducerProperties(properties);
        
        // Then
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }

    @Test
    @DisplayName("Test validateConsumerProperties with valid configuration")
    public void testValidateConsumerPropertiesWithValidConfiguration() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092,localhost:9093");
        properties.put("group.id", "test-group");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("auto.offset.reset", "earliest");
        properties.put("enable.auto.commit", "false");
        
        // When
        ValidationResult result = validator.validateConsumerProperties(properties);
        
        // Then
        assertTrue(result.isValid());
        assertFalse(result.hasErrors());
    }

    @Test
    @DisplayName("Test validateCustom with custom validator")
    public void testValidateCustomWithCustomValidator() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("custom.property", "invalid-value");
        
        ConfigurationValidator.PropertyValidator customValidator = (propertyName, value) -> {
            if ("invalid-value".equals(value)) {
                return ValidationResult.error(new ValidationError(
                    propertyName, 
                    "Custom validation failed", 
                    ValidationErrorType.INVALID_VALUE
                ));
            }
            return ValidationResult.success();
        };
        
        // When
        ValidationResult result = validator.validateCustom(properties, "custom.property", customValidator);
        
        // Then
        assertFalse(result.isValid());
        assertTrue(result.hasErrors());
        assertEquals(1, result.getErrors().size());
        assertEquals("custom.property", result.getErrors().get(0).getPropertyName());
    }
}