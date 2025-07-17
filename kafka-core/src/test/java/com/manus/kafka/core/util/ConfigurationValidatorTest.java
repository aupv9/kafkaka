package com.manus.kafka.core.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

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
    
    // Additional parameterized tests for edge cases
    
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    @DisplayName("Test validateRequiredProperties with empty or whitespace values")
    public void testValidateRequiredPropertiesEmptyValues(String value) {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", value);
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> ConfigurationValidator.validateRequiredProperties(properties, "bootstrap.servers", "group.id"));
        assertTrue(exception.getMessage().contains("group.id"));
    }
    
    @ParameterizedTest
    @CsvSource({
        "acks, 0, '0,1,all'",
        "acks, 1, '0,1,all'", 
        "acks, all, '0,1,all'",
        "security.protocol, PLAINTEXT, 'PLAINTEXT,SSL,SASL_PLAINTEXT,SASL_SSL'",
        "security.protocol, SSL, 'PLAINTEXT,SSL,SASL_PLAINTEXT,SASL_SSL'",
        "auto.offset.reset, earliest, 'earliest,latest,none'",
        "auto.offset.reset, latest, 'earliest,latest,none'",
        "auto.offset.reset, none, 'earliest,latest,none'"
    })
    @DisplayName("Test validateAllowedValues with various valid combinations")
    public void testValidateAllowedValuesParameterized(String property, String value, String allowedValues) {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put(property, value);
        String[] allowed = allowedValues.split(",");
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateAllowedValues(properties, property, allowed));
    }
    
    @ParameterizedTest
    @CsvSource({
        "acks, invalid, '0,1,all'",
        "security.protocol, INVALID, 'PLAINTEXT,SSL,SASL_PLAINTEXT,SASL_SSL'",
        "auto.offset.reset, wrong, 'earliest,latest,none'"
    })
    @DisplayName("Test validateAllowedValues with various invalid combinations")
    public void testValidateAllowedValuesInvalidParameterized(String property, String value, String allowedValues) {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put(property, value);
        String[] allowed = allowedValues.split(",");
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, 
            () -> ConfigurationValidator.validateAllowedValues(properties, property, allowed));
        assertTrue(exception.getMessage().contains(property));
        assertTrue(exception.getMessage().contains(value));
    }
    
    @ParameterizedTest
    @MethodSource("provideRangeTestData")
    @DisplayName("Test validateRange with various edge cases")
    public void testValidateRangeEdgeCases(String property, Object value, long min, long max, boolean shouldPass) {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put(property, value);
        
        // When/Then
        if (shouldPass) {
            assertDoesNotThrow(() -> ConfigurationValidator.validateRange(properties, property, min, max));
        } else {
            assertThrows(IllegalStateException.class, 
                () -> ConfigurationValidator.validateRange(properties, property, min, max));
        }
    }
    
    private static Stream<Arguments> provideRangeTestData() {
        return Stream.of(
            // Edge cases for boundary values
            Arguments.of("timeout.ms", 1000, 1000L, 60000L, true),  // Min boundary
            Arguments.of("timeout.ms", 60000, 1000L, 60000L, true), // Max boundary
            Arguments.of("timeout.ms", 999, 1000L, 60000L, false),  // Below min
            Arguments.of("timeout.ms", 60001, 1000L, 60000L, false), // Above max
            
            // Different numeric types
            Arguments.of("timeout.ms", 30000L, 1000L, 60000L, true), // Long value
            Arguments.of("timeout.ms", 30000.0, 1000L, 60000L, true), // Double value
            Arguments.of("timeout.ms", 30000.5, 1000L, 60000L, true), // Double with decimal
            
            // String representations
            Arguments.of("timeout.ms", "30000", 1000L, 60000L, true), // Valid string
            Arguments.of("timeout.ms", "30000.0", 1000L, 60000L, true), // String with decimal
            Arguments.of("timeout.ms", "not-a-number", 1000L, 60000L, false), // Invalid string
            Arguments.of("timeout.ms", "", 1000L, 60000L, false), // Empty string
            
            // Negative ranges
            Arguments.of("offset", -100, -1000L, 1000L, true), // Negative value in range
            Arguments.of("offset", -1001, -1000L, 1000L, false), // Below negative min
            
            // Zero boundaries
            Arguments.of("count", 0, 0L, 100L, true), // Zero as min boundary
            Arguments.of("count", -1, 0L, 100L, false) // Below zero min
        );
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"localhost:9092", "localhost:9092,localhost:9093", "host1:9092,host2:9092,host3:9092"})
    @DisplayName("Test bootstrap servers validation with various configurations")
    public void testBootstrapServersValidation(String bootstrapServers) {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", bootstrapServers);
        
        // When
        List<String> warnings = ConfigurationValidator.checkForWarnings(properties);
        
        // Then
        if (bootstrapServers.contains(",")) {
            // Multiple servers should not generate warnings
            assertTrue(warnings.stream().noneMatch(w -> w.contains("bootstrap server")));
        } else {
            // Single server should generate warning
            assertTrue(warnings.stream().anyMatch(w -> w.contains("bootstrap server")));
        }
    }
    
    @ParameterizedTest
    @CsvSource({
        "true, true",
        "false, false", 
        "TRUE, true",
        "FALSE, false",
        "True, true",
        "False, false"
    })
    @DisplayName("Test boolean property validation with various string representations")
    public void testBooleanPropertyValidation(String value, boolean expectedWarning) {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("enable.auto.commit", value);
        
        // When
        List<String> warnings = ConfigurationValidator.checkForWarnings(properties);
        
        // Then
        boolean hasAutoCommitWarning = warnings.stream().anyMatch(w -> w.contains("enable.auto.commit"));
        assertEquals(expectedWarning, hasAutoCommitWarning);
    }
    
    @ParameterizedTest
    @CsvSource({
        "org.apache.kafka.common.serialization.StringSerializer, org.apache.kafka.common.serialization.Serializer, true",
        "org.apache.kafka.common.serialization.StringDeserializer, org.apache.kafka.common.serialization.Deserializer, true",
        "java.lang.String, org.apache.kafka.common.serialization.Serializer, false",
        "com.nonexistent.Class, org.apache.kafka.common.serialization.Serializer, false"
    })
    @DisplayName("Test validateClass with various class combinations")
    public void testValidateClassParameterized(String className, String expectedTypeName, boolean shouldPass) throws ClassNotFoundException {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("key.serializer", className);
        Class<?> expectedType = Class.forName(expectedTypeName);
        
        // When/Then
        if (shouldPass) {
            assertDoesNotThrow(() -> ConfigurationValidator.validateClass(properties, "key.serializer", expectedType));
        } else {
            assertThrows(IllegalStateException.class, 
                () -> ConfigurationValidator.validateClass(properties, "key.serializer", expectedType));
        }
    }
    
    @Test
    @DisplayName("Test validateClass with property not set")
    public void testValidateClassNotSet() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateClass(properties, "key.serializer", 
            org.apache.kafka.common.serialization.Serializer.class));
    }
    
    @Test
    @DisplayName("Test validateClass with null property value")
    public void testValidateClassNullValue() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("key.serializer", null);
        
        // When/Then
        assertDoesNotThrow(() -> ConfigurationValidator.validateClass(properties, "key.serializer", 
            org.apache.kafka.common.serialization.Serializer.class));
    }
    
    @ParameterizedTest
    @MethodSource("provideComplexConfigurationTestData")
    @DisplayName("Test complex configuration scenarios with multiple validations")
    public void testComplexConfigurationScenarios(Map<String, Object> properties, boolean shouldPass, String expectedErrorMessage) {
        // When/Then
        if (shouldPass) {
            assertDoesNotThrow(() -> {
                ConfigurationValidator.validateRequiredProperties(properties, "bootstrap.servers");
                if (properties.containsKey("acks")) {
                    ConfigurationValidator.validateAllowedValues(properties, "acks", "0", "1", "all");
                }
                if (properties.containsKey("request.timeout.ms")) {
                    ConfigurationValidator.validateRange(properties, "request.timeout.ms", 1000, 300000);
                }
            });
        } else {
            IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
                ConfigurationValidator.validateRequiredProperties(properties, "bootstrap.servers");
                if (properties.containsKey("acks")) {
                    ConfigurationValidator.validateAllowedValues(properties, "acks", "0", "1", "all");
                }
                if (properties.containsKey("request.timeout.ms")) {
                    ConfigurationValidator.validateRange(properties, "request.timeout.ms", 1000, 300000);
                }
            });
            assertTrue(exception.getMessage().contains(expectedErrorMessage));
        }
    }
    
    private static Stream<Arguments> provideComplexConfigurationTestData() {
        return Stream.of(
            // Valid configurations
            Arguments.of(createValidProducerConfig(), true, ""),
            Arguments.of(createValidConsumerConfig(), true, ""),
            
            // Invalid configurations
            Arguments.of(createInvalidConfigMissingBootstrap(), false, "bootstrap.servers"),
            Arguments.of(createInvalidConfigBadAcks(), false, "acks"),
            Arguments.of(createInvalidConfigBadTimeout(), false, "request.timeout.ms")
        );
    }
    
    private static Map<String, Object> createValidProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("acks", "all");
        config.put("request.timeout.ms", 30000);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return config;
    }
    
    private static Map<String, Object> createValidConsumerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092,localhost:9093");
        config.put("group.id", "test-group");
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", "false");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return config;
    }
    
    private static Map<String, Object> createInvalidConfigMissingBootstrap() {
        Map<String, Object> config = new HashMap<>();
        config.put("acks", "all");
        config.put("request.timeout.ms", 30000);
        return config;
    }
    
    private static Map<String, Object> createInvalidConfigBadAcks() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("acks", "invalid");
        config.put("request.timeout.ms", 30000);
        return config;
    }
    
    private static Map<String, Object> createInvalidConfigBadTimeout() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("acks", "all");
        config.put("request.timeout.ms", 500); // Below minimum
        return config;
    }
    
    @ParameterizedTest
    @MethodSource("provideWarningTestData")
    @DisplayName("Test warning generation with various configuration combinations")
    public void testWarningGenerationScenarios(Map<String, Object> properties, int expectedWarningCount, List<String> expectedWarningKeywords) {
        // When
        List<String> warnings = ConfigurationValidator.checkForWarnings(properties);
        
        // Then
        assertEquals(expectedWarningCount, warnings.size());
        for (String keyword : expectedWarningKeywords) {
            assertTrue(warnings.stream().anyMatch(w -> w.contains(keyword)), 
                "Expected warning containing '" + keyword + "' but got: " + warnings);
        }
    }
    
    private static Stream<Arguments> provideWarningTestData() {
        return Stream.of(
            // No warnings scenario
            Arguments.of(createOptimalConfig(), 0, List.of()),
            
            // Single warning scenarios
            Arguments.of(createSingleBootstrapConfig(), 1, List.of("bootstrap server")),
            Arguments.of(createAcksZeroConfig(), 1, List.of("acks=0")),
            Arguments.of(createNoRetriesConfig(), 1, List.of("retries=0")),
            Arguments.of(createAutoCommitConfig(), 1, List.of("enable.auto.commit=true")),
            Arguments.of(createLatestOffsetConfig(), 1, List.of("auto.offset.reset=latest")),
            
            // Multiple warnings scenario
            Arguments.of(createMultipleWarningsConfig(), 5, 
                List.of("bootstrap server", "acks=0", "retries=0", "enable.auto.commit=true", "auto.offset.reset=latest"))
        );
    }
    
    private static Map<String, Object> createOptimalConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092,localhost:9093");
        config.put("acks", "all");
        config.put("retries", 3);
        config.put("enable.auto.commit", "false");
        config.put("auto.offset.reset", "earliest");
        return config;
    }
    
    private static Map<String, Object> createSingleBootstrapConfig() {
        Map<String, Object> config = createOptimalConfig();
        config.put("bootstrap.servers", "localhost:9092");
        return config;
    }
    
    private static Map<String, Object> createAcksZeroConfig() {
        Map<String, Object> config = createOptimalConfig();
        config.put("acks", "0");
        return config;
    }
    
    private static Map<String, Object> createNoRetriesConfig() {
        Map<String, Object> config = createOptimalConfig();
        config.put("retries", 0);
        return config;
    }
    
    private static Map<String, Object> createAutoCommitConfig() {
        Map<String, Object> config = createOptimalConfig();
        config.put("enable.auto.commit", "true");
        return config;
    }
    
    private static Map<String, Object> createLatestOffsetConfig() {
        Map<String, Object> config = createOptimalConfig();
        config.put("auto.offset.reset", "latest");
        return config;
    }
    
    private static Map<String, Object> createMultipleWarningsConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("acks", "0");
        config.put("retries", 0);
        config.put("enable.auto.commit", "true");
        config.put("auto.offset.reset", "latest");
        return config;
    }
}