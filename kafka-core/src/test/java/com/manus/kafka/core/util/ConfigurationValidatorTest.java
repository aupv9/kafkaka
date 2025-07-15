package com.manus.kafka.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConfigurationValidator}.
 */
public class ConfigurationValidatorTest {

    @Test
    @DisplayName("Test validateRequiredProperties with all required properties present")
    public void testValidateRequiredPropertiesAllPresent() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "test-group");
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateRequiredProperties(properties, "bootstrap.servers", "group.id"));
    }
    
    @Test
    @DisplayName("Test validateRequiredProperties with missing properties")
    public void testValidateRequiredPropertiesMissing() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> ConfigurationValidator.validateRequiredProperties(properties, "bootstrap.servers", "group.id"));
        assertTrue(exception.getMessage().contains("group.id"));
    }
    
    @Test
    @DisplayName("Test validateRequiredProperties with null property value")
    public void testValidateRequiredPropertiesNullValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", null);
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> ConfigurationValidator.validateRequiredProperties(properties, "bootstrap.servers", "group.id"));
        assertTrue(exception.getMessage().contains("group.id"));
    }
    
    @Test
    @DisplayName("Test validateAllowedValues with valid value")
    public void testValidateAllowedValuesValid() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("acks", "all");
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateAllowedValues(properties, "acks", "0", "1", "all"));
    }
    
    @Test
    @DisplayName("Test validateAllowedValues with invalid value")
    public void testValidateAllowedValuesInvalid() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("acks", "invalid");
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> ConfigurationValidator.validateAllowedValues(properties, "acks", "0", "1", "all"));
        assertTrue(exception.getMessage().contains("acks"));
        assertTrue(exception.getMessage().contains("invalid"));
    }
    
    @Test
    @DisplayName("Test validateAllowedValues with property not set")
    public void testValidateAllowedValuesNotSet() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateAllowedValues(properties, "acks", "0", "1", "all"));
    }
    
    @Test
    @DisplayName("Test validateAllowedValues with null property value")
    public void testValidateAllowedValuesNullValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("acks", null);
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateAllowedValues(properties, "acks", "0", "1", "all"));
    }
    
    @Test
    @DisplayName("Test validateRange with valid value")
    public void testValidateRangeValid() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("request.timeout.ms", 30000);
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateRange(properties, "request.timeout.ms", 1000, 60000));
    }
    
    @Test
    @DisplayName("Test validateRange with value too low")
    public void testValidateRangeTooLow() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("request.timeout.ms", 500);
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> ConfigurationValidator.validateRange(properties, "request.timeout.ms", 1000, 60000));
        assertTrue(exception.getMessage().contains("request.timeout.ms"));
        assertTrue(exception.getMessage().contains("between 1000 and 60000"));
    }
    
    @Test
    @DisplayName("Test validateRange with value too high")
    public void testValidateRangeTooHigh() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("request.timeout.ms", 70000);
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> ConfigurationValidator.validateRange(properties, "request.timeout.ms", 1000, 60000));
        assertTrue(exception.getMessage().contains("request.timeout.ms"));
        assertTrue(exception.getMessage().contains("between 1000 and 60000"));
    }
    
    @Test
    @DisplayName("Test validateRange with property not set")
    public void testValidateRangeNotSet() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateRange(properties, "request.timeout.ms", 1000, 60000));
    }
    
    @Test
    @DisplayName("Test validateRange with null property value")
    public void testValidateRangeNullValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("request.timeout.ms", null);
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateRange(properties, "request.timeout.ms", 1000, 60000));
    }
    
    @Test
    @DisplayName("Test validateRange with string value")
    public void testValidateRangeStringValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("request.timeout.ms", "30000");
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateRange(properties, "request.timeout.ms", 1000, 60000));
    }
    
    @Test
    @DisplayName("Test validateRange with invalid string value")
    public void testValidateRangeInvalidStringValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("request.timeout.ms", "invalid");
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> ConfigurationValidator.validateRange(properties, "request.timeout.ms", 1000, 60000));
        assertTrue(exception.getMessage().contains("request.timeout.ms"));
        assertTrue(exception.getMessage().contains("Invalid numeric value"));
    }
    
    @Test
    @DisplayName("Test checkForWarnings with no warnings")
    public void testCheckForWarningsNoWarnings() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092,localhost:9093");
        properties.put("acks", "all");
        properties.put("retries", 3);
        properties.put("enable.auto.commit", "false");
        properties.put("auto.offset.reset", "earliest");
        
        // When
        List<String> warnings = ConfigurationValidator.checkForWarnings(properties);
        
        // Then
        assertTrue(warnings.isEmpty());
    }
    
    @Test
    @DisplayName("Test checkForWarnings with warnings")
    public void testCheckForWarningsWithWarnings() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("acks", "0");
        properties.put("retries", 0);
        properties.put("enable.auto.commit", "true");
        properties.put("auto.offset.reset", "latest");
        
        // When
        List<String> warnings = ConfigurationValidator.checkForWarnings(properties);
        
        // Then
        assertEquals(5, warnings.size());
        assertTrue(warnings.stream().anyMatch(w -> w.contains("bootstrap server")));
        assertTrue(warnings.stream().anyMatch(w -> w.contains("acks=0")));
        assertTrue(warnings.stream().anyMatch(w -> w.contains("retries=0")));
        assertTrue(warnings.stream().anyMatch(w -> w.contains("enable.auto.commit=true")));
        assertTrue(warnings.stream().anyMatch(w -> w.contains("auto.offset.reset=latest")));
    }
}