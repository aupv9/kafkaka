package com.manus.kafka.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KafkaProducerProperties}.
 */
public class KafkaProducerPropertiesTest {

    @Test
    @DisplayName("Test default values are set correctly")
    public void testDefaultValues() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        Map<String, Object> map = properties.asMap();

        // Then
        assertEquals("all", map.get("acks"));
        assertEquals(3, map.get("retries"));
        assertEquals(16384, map.get("batch.size"));
        assertEquals(5, map.get("linger.ms")); // Integer value, not Long
        assertEquals(33554432, map.get("buffer.memory")); // Integer value, not Long
    }

    @Test
    @DisplayName("Test setting acks")
    public void testAcks() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        properties.acks("1");

        // Then
        assertEquals("1", properties.asMap().get("acks"));
    }

    @Test
    @DisplayName("Test setting retries")
    public void testRetries() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        properties.retries(10);

        // Then
        assertEquals(10, properties.asMap().get("retries"));
    }

    @Test
    @DisplayName("Test setting batch size")
    public void testBatchSize() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        properties.batchSize(32768);

        // Then
        assertEquals(32768, properties.asMap().get("batch.size"));
    }

    @Test
    @DisplayName("Test setting linger ms")
    public void testLingerMs() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        properties.lingerMs(10);

        // Then
        assertEquals(10L, properties.asMap().get("linger.ms"));
    }

    @Test
    @DisplayName("Test setting buffer memory")
    public void testBufferMemory() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        properties.bufferMemory(67108864);

        // Then
        assertEquals(67108864L, properties.asMap().get("buffer.memory"));
    }

    @Test
    @DisplayName("Test setting key serializer")
    public void testKeySerializer() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        String serializer = "org.apache.kafka.common.serialization.StringSerializer";

        // When
        properties.keySerializer(serializer);

        // Then
        assertEquals(serializer, properties.asMap().get("key.serializer"));
    }

    @Test
    @DisplayName("Test setting value serializer")
    public void testValueSerializer() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        String serializer = "org.apache.kafka.common.serialization.StringSerializer";

        // When
        properties.valueSerializer(serializer);

        // Then
        assertEquals(serializer, properties.asMap().get("value.serializer"));
    }

    @Test
    @DisplayName("Test setting compression type")
    public void testCompressionType() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        properties.compressionType("gzip");

        // Then
        assertEquals("gzip", properties.asMap().get("compression.type"));
    }

    @Test
    @DisplayName("Test setting max request size")
    public void testMaxRequestSize() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        properties.maxRequestSize(2097152);

        // Then
        assertEquals(2097152, properties.asMap().get("max.request.size"));
    }

    @Test
    @DisplayName("Test setting request timeout")
    public void testRequestTimeoutMs() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        properties.requestTimeoutMs(30000);

        // Then
        assertEquals(30000, properties.asMap().get("request.timeout.ms"));
    }

    @Test
    @DisplayName("Test setting custom property")
    public void testProperty() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();

        // When
        properties.property("custom.property", "custom-value");

        // Then
        assertEquals("custom-value", properties.asMap().get("custom.property"));
    }

    @Test
    @DisplayName("Test setting multiple custom properties")
    public void testProperties() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("prop1", "value1");
        customProps.put("prop2", "value2");

        // When
        properties.properties(customProps);

        // Then
        assertEquals("value1", properties.asMap().get("prop1"));
        assertEquals("value2", properties.asMap().get("prop2"));
    }

    @Test
    @DisplayName("Test validate passes with required properties")
    public void testValidateWithRequiredProperties() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.keySerializer("org.apache.kafka.common.serialization.StringSerializer");
        properties.valueSerializer("org.apache.kafka.common.serialization.StringSerializer");

        // When/Then
        assertDoesNotThrow(() -> properties.validate());
    }

    @Test
    @DisplayName("Test validate throws exception without bootstrap servers")
    public void testValidateWithoutBootstrapServers() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        properties.keySerializer("org.apache.kafka.common.serialization.StringSerializer");
        properties.valueSerializer("org.apache.kafka.common.serialization.StringSerializer");

        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("bootstrap.servers must be configured", exception.getMessage());
    }

    @Test
    @DisplayName("Test validate throws exception without key serializer")
    public void testValidateWithoutKeySerializer() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.valueSerializer("org.apache.kafka.common.serialization.StringSerializer");

        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("key.serializer must be configured", exception.getMessage());
    }

    @Test
    @DisplayName("Test validate throws exception without value serializer")
    public void testValidateWithoutValueSerializer() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.keySerializer("org.apache.kafka.common.serialization.StringSerializer");

        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("value.serializer must be configured", exception.getMessage());
    }
}
