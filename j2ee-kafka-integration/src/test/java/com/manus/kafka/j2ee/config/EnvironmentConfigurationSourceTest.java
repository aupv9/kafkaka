package com.manus.kafka.j2ee.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.condition.EnabledIf;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link EnvironmentConfigurationSource}.
 * These tests use actual environment variables and are designed to work
 * without mocking System.getenv() which is not supported by Mockito.
 */
public class EnvironmentConfigurationSourceTest {

    @Test
    @DisplayName("Test constructor with default prefix")
    public void testConstructorWithDefaultPrefix() {
        // When
        EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();

        // Then
        assertEquals("KAFKA_", source.getPrefix());
        Map<String, Object> properties = source.getProperties();
        assertNotNull(properties);
        // Properties will contain any environment variables that start with KAFKA_
        // We can't predict what will be there, so we just verify the structure
    }

    @Test
    @DisplayName("Test constructor with custom prefix")
    public void testConstructorWithCustomPrefix() {
        // When
        EnvironmentConfigurationSource source = new EnvironmentConfigurationSource("CUSTOM_PREFIX_");

        // Then
        assertEquals("CUSTOM_PREFIX_", source.getPrefix());
        Map<String, Object> properties = source.getProperties();
        assertNotNull(properties);
        // Properties will contain any environment variables that start with CUSTOM_PREFIX_
    }

    @Test
    @DisplayName("Test reload method")
    public void testReload() {
        // Given
        EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();
        Map<String, Object> initialProperties = source.getProperties();
        
        // When
        source.reload();
        
        // Then
        Map<String, Object> reloadedProperties = source.getProperties();
        assertNotNull(reloadedProperties);
        // The properties should be the same after reload since environment hasn't changed
        assertEquals(initialProperties.size(), reloadedProperties.size());
    }

    @Test
    @DisplayName("Test getProperty method")
    public void testGetProperty() {
        // Given
        EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();
        
        // When/Then
        // We can't predict what environment variables exist, so we test the method behavior
        Object result = source.getProperty("non.existent.property");
        assertNull(result);
        
        // Test that the method doesn't throw exceptions
        assertDoesNotThrow(() -> source.getProperty("bootstrap.servers"));
    }

    @Test
    @DisplayName("Test getPropertyAsString method")
    public void testGetPropertyAsString() {
        // Given
        EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();
        
        // When/Then
        String result = source.getPropertyAsString("non.existent.property");
        assertNull(result);
        
        // Test that the method doesn't throw exceptions
        assertDoesNotThrow(() -> source.getPropertyAsString("bootstrap.servers"));
    }

    @Test
    @DisplayName("Test getPropertyAsBoolean method")
    public void testGetPropertyAsBoolean() {
        // Given
        EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();
        
        // When/Then
        Boolean result = source.getPropertyAsBoolean("non.existent.property");
        assertNull(result);
        
        // Test that the method doesn't throw exceptions
        assertDoesNotThrow(() -> source.getPropertyAsBoolean("enable.auto.commit"));
    }

    @Test
    @DisplayName("Test property name transformation")
    @EnabledIf("hasTestEnvironmentVariable")
    public void testPropertyNameTransformation() {
        // This test only runs if we have a test environment variable set
        // Set KAFKA_TEST_PROPERTY=test-value to enable this test
        
        // Given
        EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();
        
        // When
        String value = source.getPropertyAsString("test.property");
        
        // Then
        assertEquals("test-value", value);
    }
    
    /**
     * Condition method for the property transformation test.
     * @return true if KAFKA_TEST_PROPERTY environment variable is set
     */
    static boolean hasTestEnvironmentVariable() {
        return System.getenv("KAFKA_TEST_PROPERTY") != null;
    }
}