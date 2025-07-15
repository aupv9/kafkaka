package com.manus.kafka.j2ee.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ConfigurationSource} default methods.
 */
public class ConfigurationSourceTest {

    /**
     * A simple implementation of ConfigurationSource for testing.
     */
    private static class TestConfigurationSource implements ConfigurationSource {
        private final Map<String, Object> properties;

        public TestConfigurationSource(Map<String, Object> properties) {
            this.properties = properties;
        }

        @Override
        public Map<String, Object> getProperties() {
            return properties;
        }
    }

    @Test
    @DisplayName("Test getProperty returns correct value")
    public void testGetProperty() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "test-group");
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Object value = source.getProperty("bootstrap.servers");

        // Then
        assertEquals("localhost:9092", value);
    }

    @Test
    @DisplayName("Test getProperty returns null for non-existent key")
    public void testGetPropertyNonExistent() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Object value = source.getProperty("non-existent");

        // Then
        assertNull(value);
    }

    @Test
    @DisplayName("Test getPropertyAsString returns correct value")
    public void testGetPropertyAsString() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("integer.value", 123);
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        String stringValue = source.getPropertyAsString("bootstrap.servers");
        String intValue = source.getPropertyAsString("integer.value");

        // Then
        assertEquals("localhost:9092", stringValue);
        assertEquals("123", intValue);
    }

    @Test
    @DisplayName("Test getPropertyAsString returns null for non-existent key")
    public void testGetPropertyAsStringNonExistent() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        String value = source.getPropertyAsString("non-existent");

        // Then
        assertNull(value);
    }

    @Test
    @DisplayName("Test getPropertyAsInteger returns correct value")
    public void testGetPropertyAsInteger() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("integer.value", "123");
        properties.put("integer.object", 456);
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Integer stringIntValue = source.getPropertyAsInteger("integer.value");
        Integer objectIntValue = source.getPropertyAsInteger("integer.object");

        // Then
        assertEquals(123, stringIntValue);
        assertEquals(456, objectIntValue);
    }

    @Test
    @DisplayName("Test getPropertyAsInteger returns null for non-existent key")
    public void testGetPropertyAsIntegerNonExistent() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Integer value = source.getPropertyAsInteger("non-existent");

        // Then
        assertNull(value);
    }

    @Test
    @DisplayName("Test getPropertyAsInteger returns null for non-integer value")
    public void testGetPropertyAsIntegerNonInteger() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("non.integer", "not-an-integer");
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Integer value = source.getPropertyAsInteger("non.integer");

        // Then
        assertNull(value);
    }

    @Test
    @DisplayName("Test getPropertyAsLong returns correct value")
    public void testGetPropertyAsLong() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("long.value", "123456789");
        properties.put("long.object", 987654321L);
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Long stringLongValue = source.getPropertyAsLong("long.value");
        Long objectLongValue = source.getPropertyAsLong("long.object");

        // Then
        assertEquals(123456789L, stringLongValue);
        assertEquals(987654321L, objectLongValue);
    }

    @Test
    @DisplayName("Test getPropertyAsLong returns null for non-existent key")
    public void testGetPropertyAsLongNonExistent() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Long value = source.getPropertyAsLong("non-existent");

        // Then
        assertNull(value);
    }

    @Test
    @DisplayName("Test getPropertyAsLong returns null for non-long value")
    public void testGetPropertyAsLongNonLong() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("non.long", "not-a-long");
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Long value = source.getPropertyAsLong("non.long");

        // Then
        assertNull(value);
    }

    @Test
    @DisplayName("Test getPropertyAsBoolean returns correct value")
    public void testGetPropertyAsBoolean() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("boolean.true.string", "true");
        properties.put("boolean.false.string", "false");
        properties.put("boolean.true.object", true);
        properties.put("boolean.false.object", false);
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Boolean stringTrueValue = source.getPropertyAsBoolean("boolean.true.string");
        Boolean stringFalseValue = source.getPropertyAsBoolean("boolean.false.string");
        Boolean objectTrueValue = source.getPropertyAsBoolean("boolean.true.object");
        Boolean objectFalseValue = source.getPropertyAsBoolean("boolean.false.object");

        // Then
        assertTrue(stringTrueValue);
        assertFalse(stringFalseValue);
        assertTrue(objectTrueValue);
        assertFalse(objectFalseValue);
    }

    @Test
    @DisplayName("Test getPropertyAsBoolean returns null for non-existent key")
    public void testGetPropertyAsBooleanNonExistent() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Boolean value = source.getPropertyAsBoolean("non-existent");

        // Then
        assertNull(value);
    }

    @Test
    @DisplayName("Test getPropertyAsBoolean returns false for non-boolean 'true' value")
    public void testGetPropertyAsBooleanNonBoolean() {
        // Given
        Map<String, Object> properties = new HashMap<>();
        properties.put("non.boolean", "not-a-boolean");
        ConfigurationSource source = new TestConfigurationSource(properties);

        // When
        Boolean value = source.getPropertyAsBoolean("non.boolean");

        // Then
        assertFalse(value);
    }
}