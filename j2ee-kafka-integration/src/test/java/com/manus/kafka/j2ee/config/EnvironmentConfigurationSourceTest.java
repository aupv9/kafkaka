package com.manus.kafka.j2ee.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EnvironmentConfigurationSource}.
 */
public class EnvironmentConfigurationSourceTest {

    private Map<String, String> mockEnv;

    @BeforeEach
    public void setUp() {
        mockEnv = new HashMap<>();
        mockEnv.put("KAFKA_BOOTSTRAP_SERVERS", "localhost:9092");
        mockEnv.put("KAFKA_GROUP_ID", "test-group");
        mockEnv.put("KAFKA_ENABLE_AUTO_COMMIT", "false");
        mockEnv.put("KAFKA_AUTO_OFFSET_RESET", "earliest");
        mockEnv.put("OTHER_VARIABLE", "other-value");
    }

    @Test
    @DisplayName("Test constructor with default prefix")
    public void testConstructorWithDefaultPrefix() {
        try (MockedStatic<System> mockedSystem = Mockito.mockStatic(System.class)) {
            // Given
            mockedSystem.when(System::getenv).thenReturn(mockEnv);

            // When
            EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();

            // Then
            assertEquals("KAFKA_", source.getPrefix());
            Map<String, Object> properties = source.getProperties();
            assertEquals(4, properties.size());
            assertEquals("localhost:9092", properties.get("bootstrap.servers"));
            assertEquals("test-group", properties.get("group.id"));
            assertEquals("false", properties.get("enable.auto.commit"));
            assertEquals("earliest", properties.get("auto.offset.reset"));
            assertFalse(properties.containsKey("other.variable"));
        }
    }

    @Test
    @DisplayName("Test constructor with custom prefix")
    public void testConstructorWithCustomPrefix() {
        try (MockedStatic<System> mockedSystem = Mockito.mockStatic(System.class)) {
            // Given
            mockEnv.put("CUSTOM_PREFIX_BOOTSTRAP_SERVERS", "custom-host:9092");
            mockedSystem.when(System::getenv).thenReturn(mockEnv);

            // When
            EnvironmentConfigurationSource source = new EnvironmentConfigurationSource("CUSTOM_PREFIX_");

            // Then
            assertEquals("CUSTOM_PREFIX_", source.getPrefix());
            Map<String, Object> properties = source.getProperties();
            assertEquals(1, properties.size());
            assertEquals("custom-host:9092", properties.get("bootstrap.servers"));
        }
    }

    @Test
    @DisplayName("Test reload method")
    public void testReload() {
        try (MockedStatic<System> mockedSystem = Mockito.mockStatic(System.class)) {
            // Given
            mockedSystem.when(System::getenv).thenReturn(mockEnv);
            EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();
            
            // When - modify the environment and reload
            Map<String, String> updatedEnv = new HashMap<>(mockEnv);
            updatedEnv.put("KAFKA_NEW_PROPERTY", "new-value");
            mockedSystem.when(System::getenv).thenReturn(updatedEnv);
            source.reload();
            
            // Then
            Map<String, Object> properties = source.getProperties();
            assertEquals(5, properties.size());
            assertEquals("new-value", properties.get("new.property"));
        }
    }

    @Test
    @DisplayName("Test getProperty method")
    public void testGetProperty() {
        try (MockedStatic<System> mockedSystem = Mockito.mockStatic(System.class)) {
            // Given
            mockedSystem.when(System::getenv).thenReturn(mockEnv);
            EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();
            
            // When/Then
            assertEquals("localhost:9092", source.getProperty("bootstrap.servers"));
            assertEquals("test-group", source.getProperty("group.id"));
            assertNull(source.getProperty("non.existent.property"));
        }
    }

    @Test
    @DisplayName("Test getPropertyAsString method")
    public void testGetPropertyAsString() {
        try (MockedStatic<System> mockedSystem = Mockito.mockStatic(System.class)) {
            // Given
            mockedSystem.when(System::getenv).thenReturn(mockEnv);
            EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();
            
            // When/Then
            assertEquals("localhost:9092", source.getPropertyAsString("bootstrap.servers"));
            assertEquals("test-group", source.getPropertyAsString("group.id"));
            assertNull(source.getPropertyAsString("non.existent.property"));
        }
    }

    @Test
    @DisplayName("Test getPropertyAsBoolean method")
    public void testGetPropertyAsBoolean() {
        try (MockedStatic<System> mockedSystem = Mockito.mockStatic(System.class)) {
            // Given
            mockedSystem.when(System::getenv).thenReturn(mockEnv);
            EnvironmentConfigurationSource source = new EnvironmentConfigurationSource();
            
            // When/Then
            assertFalse(source.getPropertyAsBoolean("enable.auto.commit"));
            assertNull(source.getPropertyAsBoolean("non.existent.property"));
        }
    }
}