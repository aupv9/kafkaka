package com.manus.kafka.springboot.properties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KafkaProperties}.
 */
public class KafkaPropertiesTest {

    @Test
    @DisplayName("Test default values")
    public void testDefaultValues() {
        // Given
        KafkaProperties properties = new KafkaProperties();
        
        // Then
        assertNull(properties.getBootstrapServers());
        assertNull(properties.getClientId());
        assertNotNull(properties.getProperties());
        assertTrue(properties.getProperties().isEmpty());
        
        // Producer defaults
        assertEquals("all", properties.getProducer().getAcks());
        assertEquals(Integer.valueOf(3), properties.getProducer().getRetries());
        assertEquals(Integer.valueOf(16384), properties.getProducer().getBatchSize());
        assertEquals(Long.valueOf(5L), properties.getProducer().getLingerMs());
        assertEquals(Long.valueOf(33554432L), properties.getProducer().getBufferMemory());
        assertNull(properties.getProducer().getKeySerializer());
        assertNull(properties.getProducer().getValueSerializer());
        assertNull(properties.getProducer().getCompressionType());
        assertNotNull(properties.getProducer().getProperties());
        assertTrue(properties.getProducer().getProperties().isEmpty());
        
        // Consumer defaults
        assertNull(properties.getConsumer().getGroupId());
        assertEquals(Boolean.FALSE, properties.getConsumer().getEnableAutoCommit());
        assertNull(properties.getConsumer().getAutoCommitIntervalMs());
        assertEquals("earliest", properties.getConsumer().getAutoOffsetReset());
        assertNull(properties.getConsumer().getKeyDeserializer());
        assertNull(properties.getConsumer().getValueDeserializer());
        assertEquals(Integer.valueOf(500), properties.getConsumer().getMaxPollRecords());
        assertNotNull(properties.getConsumer().getProperties());
        assertTrue(properties.getConsumer().getProperties().isEmpty());
        
        // Admin defaults
        assertFalse(properties.getAdmin().isAutoCreate());
        assertNotNull(properties.getAdmin().getProperties());
        assertTrue(properties.getAdmin().getProperties().isEmpty());
        
        // Security defaults
        assertNull(properties.getSecurity().getProtocol());
        assertNull(properties.getSecurity().getSaslMechanism());
        assertNull(properties.getSecurity().getSaslJaasConfig());
    }

    @Test
    @DisplayName("Test setters and getters")
    public void testSettersAndGetters() {
        // Given
        KafkaProperties properties = new KafkaProperties();
        
        // When
        properties.setBootstrapServers("localhost:9092");
        properties.setClientId("test-client");
        
        Map<String, String> commonProps = new HashMap<>();
        commonProps.put("common.prop", "common-value");
        properties.setProperties(commonProps);
        
        // Producer properties
        KafkaProperties.Producer producer = properties.getProducer();
        producer.setAcks("1");
        producer.setRetries(5);
        producer.setBatchSize(32768);
        producer.setLingerMs(10L);
        producer.setBufferMemory(67108864L);
        producer.setKeySerializer("org.apache.kafka.common.serialization.StringSerializer");
        producer.setValueSerializer("org.apache.kafka.common.serialization.StringSerializer");
        producer.setCompressionType("gzip");
        
        Map<String, String> producerProps = new HashMap<>();
        producerProps.put("producer.prop", "producer-value");
        producer.setProperties(producerProps);
        
        // Consumer properties
        KafkaProperties.Consumer consumer = properties.getConsumer();
        consumer.setGroupId("test-group");
        consumer.setEnableAutoCommit(true);
        consumer.setAutoCommitIntervalMs(5000);
        consumer.setAutoOffsetReset("latest");
        consumer.setKeyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        consumer.setValueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        consumer.setMaxPollRecords(1000);
        
        Map<String, String> consumerProps = new HashMap<>();
        consumerProps.put("consumer.prop", "consumer-value");
        consumer.setProperties(consumerProps);
        
        // Admin properties
        KafkaProperties.Admin admin = properties.getAdmin();
        admin.setAutoCreate(true);
        
        Map<String, String> adminProps = new HashMap<>();
        adminProps.put("admin.prop", "admin-value");
        admin.setProperties(adminProps);
        
        // Security properties
        KafkaProperties.Security security = properties.getSecurity();
        security.setProtocol("SASL_SSL");
        security.setSaslMechanism("PLAIN");
        security.setSaslJaasConfig("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");
        
        // Then
        assertEquals("localhost:9092", properties.getBootstrapServers());
        assertEquals("test-client", properties.getClientId());
        assertEquals("common-value", properties.getProperties().get("common.prop"));
        
        // Producer properties
        assertEquals("1", properties.getProducer().getAcks());
        assertEquals(Integer.valueOf(5), properties.getProducer().getRetries());
        assertEquals(Integer.valueOf(32768), properties.getProducer().getBatchSize());
        assertEquals(Long.valueOf(10L), properties.getProducer().getLingerMs());
        assertEquals(Long.valueOf(67108864L), properties.getProducer().getBufferMemory());
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", properties.getProducer().getKeySerializer());
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", properties.getProducer().getValueSerializer());
        assertEquals("gzip", properties.getProducer().getCompressionType());
        assertEquals("producer-value", properties.getProducer().getProperties().get("producer.prop"));
        
        // Consumer properties
        assertEquals("test-group", properties.getConsumer().getGroupId());
        assertEquals(Boolean.TRUE, properties.getConsumer().getEnableAutoCommit());
        assertEquals(Integer.valueOf(5000), properties.getConsumer().getAutoCommitIntervalMs());
        assertEquals("latest", properties.getConsumer().getAutoOffsetReset());
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", properties.getConsumer().getKeyDeserializer());
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", properties.getConsumer().getValueDeserializer());
        assertEquals(Integer.valueOf(1000), properties.getConsumer().getMaxPollRecords());
        assertEquals("consumer-value", properties.getConsumer().getProperties().get("consumer.prop"));
        
        // Admin properties
        assertTrue(properties.getAdmin().isAutoCreate());
        assertEquals("admin-value", properties.getAdmin().getProperties().get("admin.prop"));
        
        // Security properties
        assertEquals("SASL_SSL", properties.getSecurity().getProtocol());
        assertEquals("PLAIN", properties.getSecurity().getSaslMechanism());
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";", 
                     properties.getSecurity().getSaslJaasConfig());
    }

    @Test
    @DisplayName("Test nested classes can be created independently")
    public void testNestedClassesCreation() {
        // Given/When
        KafkaProperties.Producer producer = new KafkaProperties.Producer();
        KafkaProperties.Consumer consumer = new KafkaProperties.Consumer();
        KafkaProperties.Admin admin = new KafkaProperties.Admin();
        KafkaProperties.Security security = new KafkaProperties.Security();
        
        // Then
        assertNotNull(producer);
        assertNotNull(consumer);
        assertNotNull(admin);
        assertNotNull(security);
    }
}