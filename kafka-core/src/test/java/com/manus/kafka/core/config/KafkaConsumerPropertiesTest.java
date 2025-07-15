package com.manus.kafka.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KafkaConsumerProperties}.
 */
public class KafkaConsumerPropertiesTest {

    @Test
    @DisplayName("Test default values are set correctly")
    public void testDefaultValues() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        Map<String, Object> map = properties.asMap();
        
        // Then
        assertEquals("false", map.get("enable.auto.commit"));
        assertEquals("earliest", map.get("auto.offset.reset"));
        assertEquals(1, map.get("fetch.min.bytes"));
        assertEquals(500, map.get("fetch.max.wait.ms"));
        assertEquals(500, map.get("max.poll.records"));
    }
    
    @Test
    @DisplayName("Test setting group ID")
    public void testGroupId() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.groupId("test-group");
        
        // Then
        assertEquals("test-group", properties.asMap().get("group.id"));
    }
    
    @Test
    @DisplayName("Test setting enable auto commit")
    public void testEnableAutoCommit() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.enableAutoCommit(true);
        
        // Then
        assertEquals("true", properties.asMap().get("enable.auto.commit"));
    }
    
    @Test
    @DisplayName("Test setting auto commit interval")
    public void testAutoCommitIntervalMs() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.autoCommitIntervalMs(5000);
        
        // Then
        assertEquals(5000, properties.asMap().get("auto.commit.interval.ms"));
    }
    
    @Test
    @DisplayName("Test setting auto offset reset")
    public void testAutoOffsetReset() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.autoOffsetReset("latest");
        
        // Then
        assertEquals("latest", properties.asMap().get("auto.offset.reset"));
    }
    
    @Test
    @DisplayName("Test setting fetch min bytes")
    public void testFetchMinBytes() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.fetchMinBytes(1024);
        
        // Then
        assertEquals(1024, properties.asMap().get("fetch.min.bytes"));
    }
    
    @Test
    @DisplayName("Test setting fetch max wait")
    public void testFetchMaxWaitMs() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.fetchMaxWaitMs(1000);
        
        // Then
        assertEquals(1000, properties.asMap().get("fetch.max.wait.ms"));
    }
    
    @Test
    @DisplayName("Test setting max poll records")
    public void testMaxPollRecords() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.maxPollRecords(1000);
        
        // Then
        assertEquals(1000, properties.asMap().get("max.poll.records"));
    }
    
    @Test
    @DisplayName("Test setting session timeout")
    public void testSessionTimeoutMs() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.sessionTimeoutMs(30000);
        
        // Then
        assertEquals(30000, properties.asMap().get("session.timeout.ms"));
    }
    
    @Test
    @DisplayName("Test setting max poll interval")
    public void testMaxPollIntervalMs() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.maxPollIntervalMs(300000);
        
        // Then
        assertEquals(300000, properties.asMap().get("max.poll.interval.ms"));
    }
    
    @Test
    @DisplayName("Test setting key deserializer")
    public void testKeyDeserializer() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        String deserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        
        // When
        properties.keyDeserializer(deserializer);
        
        // Then
        assertEquals(deserializer, properties.asMap().get("key.deserializer"));
    }
    
    @Test
    @DisplayName("Test setting value deserializer")
    public void testValueDeserializer() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        String deserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        
        // When
        properties.valueDeserializer(deserializer);
        
        // Then
        assertEquals(deserializer, properties.asMap().get("value.deserializer"));
    }
    
    @Test
    @DisplayName("Test setting custom property")
    public void testProperty() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        
        // When
        properties.property("custom.property", "custom-value");
        
        // Then
        assertEquals("custom-value", properties.asMap().get("custom.property"));
    }
    
    @Test
    @DisplayName("Test setting multiple custom properties")
    public void testProperties() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
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
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.groupId("test-group");
        properties.keyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        properties.valueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        
        // When/Then
        assertDoesNotThrow(() -> properties.validate());
    }
    
    @Test
    @DisplayName("Test validate throws exception without bootstrap servers")
    public void testValidateWithoutBootstrapServers() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.groupId("test-group");
        properties.keyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        properties.valueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("bootstrap.servers must be configured", exception.getMessage());
    }
    
    @Test
    @DisplayName("Test validate throws exception without group ID")
    public void testValidateWithoutGroupId() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.keyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        properties.valueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("group.id must be configured", exception.getMessage());
    }
    
    @Test
    @DisplayName("Test validate throws exception without key deserializer")
    public void testValidateWithoutKeyDeserializer() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.groupId("test-group");
        properties.valueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("key.deserializer must be configured", exception.getMessage());
    }
    
    @Test
    @DisplayName("Test validate throws exception without value deserializer")
    public void testValidateWithoutValueDeserializer() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.groupId("test-group");
        properties.keyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("value.deserializer must be configured", exception.getMessage());
    }
}