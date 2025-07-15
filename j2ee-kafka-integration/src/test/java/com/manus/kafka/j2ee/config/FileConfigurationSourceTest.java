package com.manus.kafka.j2ee.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link FileConfigurationSource}.
 */
public class FileConfigurationSourceTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("Test constructor loads properties from file")
    public void testConstructorLoadsProperties() throws IOException {
        // Given
        File propertiesFile = tempDir.resolve("test.properties").toFile();
        try (FileWriter writer = new FileWriter(propertiesFile)) {
            writer.write("bootstrap.servers=localhost:9092\n");
            writer.write("group.id=test-group\n");
            writer.write("key.deserializer=org.apache.kafka.common.serialization.StringDeserializer\n");
            writer.write("value.deserializer=org.apache.kafka.common.serialization.StringDeserializer\n");
        }

        // When
        FileConfigurationSource source = new FileConfigurationSource(propertiesFile.getAbsolutePath());

        // Then
        assertEquals(propertiesFile.getAbsolutePath(), source.getFilePath());
        Map<String, Object> properties = source.getProperties();
        assertEquals(4, properties.size());
        assertEquals("localhost:9092", properties.get("bootstrap.servers"));
        assertEquals("test-group", properties.get("group.id"));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", properties.get("key.deserializer"));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", properties.get("value.deserializer"));
    }

    @Test
    @DisplayName("Test reload method refreshes properties")
    public void testReload() throws IOException {
        // Given
        File propertiesFile = tempDir.resolve("test.properties").toFile();
        try (FileWriter writer = new FileWriter(propertiesFile)) {
            writer.write("bootstrap.servers=localhost:9092\n");
            writer.write("group.id=test-group\n");
        }

        FileConfigurationSource source = new FileConfigurationSource(propertiesFile.getAbsolutePath());
        assertEquals(2, source.getProperties().size());

        // When - modify the file and reload
        try (FileWriter writer = new FileWriter(propertiesFile)) {
            writer.write("bootstrap.servers=localhost:9093\n");
            writer.write("group.id=new-group\n");
            writer.write("new.property=new-value\n");
        }
        source.reload();

        // Then
        Map<String, Object> properties = source.getProperties();
        assertEquals(3, properties.size());
        assertEquals("localhost:9093", properties.get("bootstrap.servers"));
        assertEquals("new-group", properties.get("group.id"));
        assertEquals("new-value", properties.get("new.property"));
    }

    @Test
    @DisplayName("Test constructor throws exception for non-existent file")
    public void testConstructorThrowsExceptionForNonExistentFile() {
        // Given
        String nonExistentFile = tempDir.resolve("non-existent.properties").toString();

        // When/Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> new FileConfigurationSource(nonExistentFile));
        assertTrue(exception.getMessage().contains("Failed to load properties from file"));
        assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    @DisplayName("Test getProperty method")
    public void testGetProperty() throws IOException {
        // Given
        File propertiesFile = tempDir.resolve("test.properties").toFile();
        try (FileWriter writer = new FileWriter(propertiesFile)) {
            writer.write("bootstrap.servers=localhost:9092\n");
            writer.write("group.id=test-group\n");
        }

        FileConfigurationSource source = new FileConfigurationSource(propertiesFile.getAbsolutePath());

        // When/Then
        assertEquals("localhost:9092", source.getProperty("bootstrap.servers"));
        assertEquals("test-group", source.getProperty("group.id"));
        assertNull(source.getProperty("non.existent.property"));
    }

    @Test
    @DisplayName("Test getPropertyAsString method")
    public void testGetPropertyAsString() throws IOException {
        // Given
        File propertiesFile = tempDir.resolve("test.properties").toFile();
        try (FileWriter writer = new FileWriter(propertiesFile)) {
            writer.write("bootstrap.servers=localhost:9092\n");
            writer.write("integer.value=123\n");
        }

        FileConfigurationSource source = new FileConfigurationSource(propertiesFile.getAbsolutePath());

        // When/Then
        assertEquals("localhost:9092", source.getPropertyAsString("bootstrap.servers"));
        assertEquals("123", source.getPropertyAsString("integer.value"));
        assertNull(source.getPropertyAsString("non.existent.property"));
    }

    @Test
    @DisplayName("Test getPropertyAsInteger method")
    public void testGetPropertyAsInteger() throws IOException {
        // Given
        File propertiesFile = tempDir.resolve("test.properties").toFile();
        try (FileWriter writer = new FileWriter(propertiesFile)) {
            writer.write("integer.value=123\n");
            writer.write("non.integer=not-an-integer\n");
        }

        FileConfigurationSource source = new FileConfigurationSource(propertiesFile.getAbsolutePath());

        // When/Then
        assertEquals(123, source.getPropertyAsInteger("integer.value"));
        assertNull(source.getPropertyAsInteger("non.integer"));
        assertNull(source.getPropertyAsInteger("non.existent.property"));
    }
}