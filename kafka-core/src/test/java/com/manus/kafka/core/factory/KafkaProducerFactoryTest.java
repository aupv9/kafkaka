package com.manus.kafka.core.factory;

import com.manus.kafka.core.config.KafkaProducerProperties;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KafkaProducerFactory}.
 */
public class KafkaProducerFactoryTest {

    @Test
    @DisplayName("Test constructor with default properties")
    public void testConstructorWithDefaultProperties() {
        // Given/When
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();

        // Then
        KafkaProducerProperties properties = factory.getProperties();
        assertNotNull(properties);
        assertEquals("all", properties.asMap().get("acks"));
        assertEquals(3, properties.asMap().get("retries"));
        assertEquals(16384, properties.asMap().get("batch.size"));
    }

    @Test
    @DisplayName("Test constructor with custom properties")
    public void testConstructorWithCustomProperties() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.acks("1");
        properties.retries(5);

        // When
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>(properties);

        // Then
        KafkaProducerProperties factoryProperties = factory.getProperties();
        assertNotNull(factoryProperties);
        assertEquals("localhost:9092", factoryProperties.asMap().get("bootstrap.servers"));
        assertEquals("1", factoryProperties.asMap().get("acks"));
        assertEquals(5, factoryProperties.asMap().get("retries"));
    }

    @Test
    @DisplayName("Test createProducer with invalid properties")
    public void testCreateProducerWithInvalidProperties() {
        // Given
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();

        // When/Then
        // The factory should validate properties before creating a Producer
        assertThrows(IllegalStateException.class, () -> factory.createProducer());
    }

    @Test
    @DisplayName("Test createProducer with custom properties")
    public void testCreateProducerWithCustomProperties() {
        // Given
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("bootstrap.servers", "localhost:9092");
        customProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        customProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // When/Then
        // We can't easily test the actual creation of a Producer without a real Kafka broker,
        // but we can verify that the method doesn't throw a validation exception
        Producer<String, String> producer = null;
        try {
            producer = factory.createProducer(customProps);
            assertNotNull(producer, "Producer should not be null");
        } catch (Exception e) {
            // We expect a connection-related exception, not a validation exception
            assertFalse(e instanceof IllegalStateException, "Unexpected IllegalStateException: " + e.getMessage());
        } finally {
            // Clean up
            if (producer != null) {
                producer.close();
            }
        }
    }

    @Test
    @DisplayName("Test createProducer with invalid custom properties")
    public void testCreateProducerWithInvalidCustomProperties() {
        // Given
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("bootstrap.servers", "localhost:9092");
        // Missing required properties: key.serializer, value.serializer

        // When/Then
        assertThrows(IllegalStateException.class, () -> factory.createProducer(customProps));
    }

    @Test
    @DisplayName("Test static createProducer method")
    public void testStaticCreateProducer() {
        // Given
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // When/Then
        // The static method doesn't validate properties or try to connect immediately,
        // so it should not throw an exception
        Producer<String, String> producer = null;
        try {
            producer = KafkaProducerFactory.createProducer(properties);
            assertNotNull(producer, "Producer should not be null");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            // Clean up
            if (producer != null) {
                producer.close();
            }
        }
    }
}
