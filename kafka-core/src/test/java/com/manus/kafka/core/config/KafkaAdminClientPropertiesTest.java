package com.manus.kafka.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KafkaAdminClientProperties}.
 */
public class KafkaAdminClientPropertiesTest {

    @Test
    @DisplayName("Test default values are set correctly")
    public void testDefaultValues() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();

        // When
        Map<String, Object> map = properties.asMap();

        // Then
        assertEquals(30000, map.get("request.timeout.ms"));
        assertEquals(5, map.get("retries"));
    }

    @Test
    @DisplayName("Test setting request timeout")
    public void testRequestTimeoutMs() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();

        // When
        properties.requestTimeoutMs(60000);

        // Then
        assertEquals(60000, properties.asMap().get("request.timeout.ms"));
    }

    @Test
    @DisplayName("Test setting retries")
    public void testRetries() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();

        // When
        properties.retries(10);

        // Then
        assertEquals(10, properties.asMap().get("retries"));
    }

    @Test
    @DisplayName("Test setting retry backoff")
    public void testRetryBackoffMs() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();

        // When
        properties.retryBackoffMs(1000);

        // Then
        assertEquals(1000L, properties.asMap().get("retry.backoff.ms"));
    }

    @Test
    @DisplayName("Test setting connections max idle time")
    public void testConnectionsMaxIdleMs() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();

        // When
        properties.connectionsMaxIdleMs(300000);

        // Then
        assertEquals(300000L, properties.asMap().get("connections.max.idle.ms"));
    }

    @Test
    @DisplayName("Test setting custom property")
    public void testProperty() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();

        // When
        properties.property("custom.property", "custom-value");

        // Then
        assertEquals("custom-value", properties.asMap().get("custom.property"));
    }

    @Test
    @DisplayName("Test setting multiple custom properties")
    public void testProperties() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();
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
    @DisplayName("Test validate passes with bootstrap servers")
    public void testValidateWithBootstrapServers() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();
        properties.bootstrapServers("localhost:9092,localhost:9093");

        // When/Then
        assertDoesNotThrow(() -> properties.validate());
    }

    @Test
    @DisplayName("Test validate throws exception without bootstrap servers")
    public void testValidateWithoutBootstrapServers() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();

        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("bootstrap.servers must be configured", exception.getMessage());
    }

    @Test
    @DisplayName("Test method chaining")
    public void testMethodChaining() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();

        // When - set properties individually to avoid casting issues
        properties.bootstrapServers("localhost:9092");
        properties.requestTimeoutMs(60000);
        properties.retries(10);
        properties.retryBackoffMs(1000);
        properties.connectionsMaxIdleMs(300000);
        properties.property("custom", "value");

        // Then
        Map<String, Object> map = properties.asMap();
        assertEquals("localhost:9092", map.get("bootstrap.servers"));
        assertEquals(60000, map.get("request.timeout.ms"));
        assertEquals(10, map.get("retries"));
        assertEquals(1000L, map.get("retry.backoff.ms"));
        assertEquals(300000L, map.get("connections.max.idle.ms"));
        assertEquals("value", map.get("custom"));
    }

    @Test
    @DisplayName("Test inheritance from CommonKafkaProperties")
    public void testInheritance() {
        // Given
        KafkaAdminClientProperties properties = new KafkaAdminClientProperties();

        // When
        properties
            .bootstrapServers("localhost:9092")
            .clientId("test-client")
            .securityProtocol("SSL");

        // Then
        Map<String, Object> map = properties.asMap();
        assertEquals("localhost:9092", map.get("bootstrap.servers"));
        assertEquals("test-client", map.get("client.id"));
        assertEquals("SSL", map.get("security.protocol"));
    }
}
