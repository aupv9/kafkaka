package com.manus.kafka.core.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConfigurationException} and its hierarchy.
 */
public class ConfigurationExceptionTest {

    @Test
    @DisplayName("Test ConfigurationException with ValidationResult")
    public void testConfigurationExceptionWithValidationResult() {
        // Given
        ValidationResult result = new ValidationResult();
        ValidationError error = ValidationError.missingRequired("test.property");
        ValidationWarning warning = ValidationWarning.performance("test.property", "Test warning", "Test suggestion");
        result.addError(error);
        result.addWarning(warning);
        
        // When
        ConfigurationException exception = new ConfigurationException(result);
        
        // Then
        assertEquals("Configuration validation failed: Required property is missing", exception.getMessage());
        assertEquals(result, exception.getValidationResult());
        assertEquals(1, exception.getErrors().size());
        assertEquals(1, exception.getWarnings().size());
        assertEquals(error, exception.getErrors().get(0));
        assertEquals(warning, exception.getWarnings().get(0));
        assertTrue(exception.hasRecoverySuggestions());
        assertEquals(2, exception.getRecoverySuggestions().size());
    }

    @Test
    @DisplayName("Test ConfigurationException with single error")
    public void testConfigurationExceptionWithSingleError() {
        // Given
        ValidationError error = ValidationError.invalidValue("test.property", "invalid", "valid");
        
        // When
        ConfigurationException exception = new ConfigurationException(error);
        
        // Then
        assertEquals("Configuration validation failed: Invalid value: invalid", exception.getMessage());
        assertEquals(1, exception.getErrors().size());
        assertEquals(error, exception.getErrors().get(0));
        assertTrue(exception.hasRecoverySuggestions());
    }

    @Test
    @DisplayName("Test ConfigurationException with message")
    public void testConfigurationExceptionWithMessage() {
        // Given
        String message = "Custom configuration error";
        
        // When
        ConfigurationException exception = new ConfigurationException(message);
        
        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(1, exception.getErrors().size());
        assertEquals(message, exception.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Test MissingRequiredPropertyException")
    public void testMissingRequiredPropertyException() {
        // Given
        List<String> missingProperties = List.of("property1", "property2");
        
        // When
        MissingRequiredPropertyException exception = new MissingRequiredPropertyException(missingProperties);
        
        // Then
        assertEquals(missingProperties, exception.getMissingProperties());
        assertEquals(2, exception.getErrors().size());
        assertTrue(exception.getMessage().contains("Configuration validation failed"));
    }

    @Test
    @DisplayName("Test MissingRequiredPropertyException with single property")
    public void testMissingRequiredPropertyExceptionSingleProperty() {
        // Given
        String propertyName = "test.property";
        
        // When
        MissingRequiredPropertyException exception = new MissingRequiredPropertyException(propertyName);
        
        // Then
        assertEquals(List.of(propertyName), exception.getMissingProperties());
        assertEquals(1, exception.getErrors().size());
        assertEquals(propertyName, exception.getErrors().get(0).getPropertyName());
    }

    @Test
    @DisplayName("Test InvalidPropertyValueException")
    public void testInvalidPropertyValueException() {
        // Given
        String propertyName = "test.property";
        Object actualValue = "invalid";
        Object expectedValue = "valid";
        
        // When
        InvalidPropertyValueException exception = new InvalidPropertyValueException(propertyName, actualValue, expectedValue);
        
        // Then
        assertEquals(propertyName, exception.getPropertyName());
        assertEquals(actualValue, exception.getActualValue());
        assertEquals(expectedValue, exception.getExpectedValue());
        assertEquals(1, exception.getErrors().size());
        assertEquals(ValidationErrorType.INVALID_VALUE, exception.getErrors().get(0).getType());
    }

    @Test
    @DisplayName("Test InvalidPropertyValueException with custom message")
    public void testInvalidPropertyValueExceptionWithCustomMessage() {
        // Given
        String propertyName = "test.property";
        Object actualValue = "invalid";
        String message = "Custom error message";
        
        // When
        InvalidPropertyValueException exception = new InvalidPropertyValueException(propertyName, actualValue, message);
        
        // Then
        assertEquals(propertyName, exception.getPropertyName());
        assertEquals(actualValue, exception.getActualValue());
        assertNull(exception.getExpectedValue());
        assertEquals(message, exception.getErrors().get(0).getMessage());
    }

    @Test
    @DisplayName("Test PropertyOutOfRangeException")
    public void testPropertyOutOfRangeException() {
        // Given
        String propertyName = "test.property";
        Object actualValue = 150;
        Object minValue = 1;
        Object maxValue = 100;
        
        // When
        PropertyOutOfRangeException exception = new PropertyOutOfRangeException(propertyName, actualValue, minValue, maxValue);
        
        // Then
        assertEquals(propertyName, exception.getPropertyName());
        assertEquals(actualValue, exception.getActualValue());
        assertEquals(minValue, exception.getMinValue());
        assertEquals(maxValue, exception.getMaxValue());
        assertEquals(1, exception.getErrors().size());
        assertEquals(ValidationErrorType.OUT_OF_RANGE, exception.getErrors().get(0).getType());
    }

    @Test
    @DisplayName("Test getDetailedMessage")
    public void testGetDetailedMessage() {
        // Given
        ValidationResult result = new ValidationResult();
        ValidationError error1 = ValidationError.missingRequired("property1");
        ValidationError error2 = ValidationError.invalidValue("property2", "invalid", "valid");
        ValidationWarning warning = ValidationWarning.performance("property3", "Performance warning", "Use multiple servers");
        
        result.addError(error1);
        result.addError(error2);
        result.addWarning(warning);
        
        ConfigurationException exception = new ConfigurationException(result);
        
        // When
        String detailedMessage = exception.getDetailedMessage();
        
        // Then
        assertTrue(detailedMessage.contains("Configuration validation failed"));
        assertTrue(detailedMessage.contains("Errors:"));
        assertTrue(detailedMessage.contains("property1"));
        assertTrue(detailedMessage.contains("property2"));
        assertTrue(detailedMessage.contains("Warnings:"));
        assertTrue(detailedMessage.contains("property3"));
        assertTrue(detailedMessage.contains("Suggestions:"));
        assertTrue(detailedMessage.contains("Use multiple servers"));
    }

    @Test
    @DisplayName("Test exception without recovery suggestions")
    public void testExceptionWithoutRecoverySuggestions() {
        // Given
        ValidationError error = new ValidationError("test.property", "Error without suggestion", ValidationErrorType.GENERAL);
        
        // When
        ConfigurationException exception = new ConfigurationException(error);
        
        // Then
        assertFalse(exception.hasRecoverySuggestions());
        assertTrue(exception.getRecoverySuggestions().isEmpty());
    }
}