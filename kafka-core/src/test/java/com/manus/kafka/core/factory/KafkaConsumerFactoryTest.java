package com.manus.kafka.core.factory;

import com.manus.kafka.core.config.KafkaConsumerProperties;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KafkaConsumerFactory}.
 */
public class KafkaConsumerFactoryTest {

    @Test
    @DisplayName("Test constructor with default properties")
    public void testConstructorWithDefaultProperties() {
        // Given/When
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();

        // Then
        KafkaConsumerProperties properties = factory.getProperties();
        assertNotNull(properties);
        assertEquals("false", properties.asMap().get("enable.auto.commit"));
        assertEquals("earliest", properties.asMap().get("auto.offset.reset"));
    }

    @Test
    @DisplayName("Test constructor with custom properties")
    public void testConstructorWithCustomProperties() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.groupId("test-group");
        properties.enableAutoCommit(true);

        // When
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>(properties);

        // Then
        KafkaConsumerProperties factoryProperties = factory.getProperties();
        assertNotNull(factoryProperties);
        assertEquals("localhost:9092", factoryProperties.asMap().get("bootstrap.servers"));
        assertEquals("test-group", factoryProperties.asMap().get("group.id"));
        assertEquals("true", factoryProperties.asMap().get("enable.auto.commit"));
    }

    @Test
    @DisplayName("Test createConsumer with invalid properties")
    public void testCreateConsumerWithInvalidProperties() {
        // Given
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();

        // When/Then
        // The factory should validate properties before creating a Consumer
        assertThrows(IllegalStateException.class, () -> factory.createConsumer());
    }

    @Test
    @DisplayName("Test createConsumer with custom properties")
    public void testCreateConsumerWithCustomProperties() {
        // Given
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("bootstrap.servers", "localhost:9092");
        customProps.put("group.id", "test-group");
        customProps.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        customProps.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // When/Then
        // We can't easily test the actual creation of a Consumer without a real Kafka broker,
        // but we can verify that the method doesn't throw a validation exception
        Consumer<String, String> consumer = null;
        try {
            consumer = factory.createConsumer(customProps);
            assertNotNull(consumer, "Consumer should not be null");
        } catch (Exception e) {
            // We expect a connection-related exception, not a validation exception
            assertFalse(e instanceof IllegalStateException, "Unexpected IllegalStateException: " + e.getMessage());
        } finally {
            // Clean up
            if (consumer != null) {
                consumer.close();
            }
        }
    }

    @Test
    @DisplayName("Test createConsumer with invalid custom properties")
    public void testCreateConsumerWithInvalidCustomProperties() {
        // Given
        KafkaConsumerFactory<String, String> factory = new KafkaConsumerFactory<>();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("bootstrap.servers", "localhost:9092");
        // Missing required properties: group.id, key.deserializer, value.deserializer

        // When/Then
        assertThrows(IllegalStateException.class, () -> factory.createConsumer(customProps));
    }

    @Test
    @DisplayName("Test static createConsumer method")
    public void testStaticCreateConsumer() {
        // Given
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "test-group");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        // When/Then
        // The static method doesn't validate properties or try to connect immediately,
        // so it should not throw an exception
        Consumer<String, String> consumer = null;
        try {
            consumer = KafkaConsumerFactory.createConsumer(properties);
            assertNotNull(consumer, "Consumer should not be null");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            // Clean up
            if (consumer != null) {
                consumer.close();
            }
        }
    }
}
