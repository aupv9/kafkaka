package com.manus.kafka.core.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CommonKafkaProperties}.
 */
public class CommonKafkaPropertiesTest {

    @Test
    @DisplayName("Test setting bootstrap servers")
    public void testBootstrapServers() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        
        // When
        properties.bootstrapServers("localhost:9092");
        
        // Then
        assertEquals("localhost:9092", properties.asMap().get("bootstrap.servers"));
    }
    
    @Test
    @DisplayName("Test setting client ID")
    public void testClientId() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        
        // When
        properties.clientId("test-client");
        
        // Then
        assertEquals("test-client", properties.asMap().get("client.id"));
    }
    
    @Test
    @DisplayName("Test setting security protocol")
    public void testSecurityProtocol() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        
        // When
        properties.securityProtocol("SSL");
        
        // Then
        assertEquals("SSL", properties.asMap().get("security.protocol"));
    }
    
    @Test
    @DisplayName("Test setting SASL mechanism")
    public void testSaslMechanism() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        
        // When
        properties.saslMechanism("PLAIN");
        
        // Then
        assertEquals("PLAIN", properties.asMap().get("sasl.mechanism"));
    }
    
    @Test
    @DisplayName("Test setting SASL JAAS config")
    public void testSaslJaasConfig() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        String jaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";";
        
        // When
        properties.saslJaasConfig(jaasConfig);
        
        // Then
        assertEquals(jaasConfig, properties.asMap().get("sasl.jaas.config"));
    }
    
    @Test
    @DisplayName("Test setting custom property")
    public void testProperty() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        
        // When
        properties.property("custom.property", "custom-value");
        
        // Then
        assertEquals("custom-value", properties.asMap().get("custom.property"));
    }
    
    @Test
    @DisplayName("Test setting multiple custom properties")
    public void testProperties() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
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
    @DisplayName("Test asMap returns a copy of the properties")
    public void testAsMapReturnsCopy() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        properties.bootstrapServers("localhost:9092");
        
        // When
        Map<String, Object> map = properties.asMap();
        map.put("new.property", "new-value");
        
        // Then
        assertFalse(properties.asMap().containsKey("new.property"));
    }
    
    @Test
    @DisplayName("Test asProperties returns a Properties object with the same values")
    public void testAsProperties() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        properties.bootstrapServers("localhost:9092");
        properties.clientId("test-client");
        
        // When
        Properties props = properties.asProperties();
        
        // Then
        assertEquals("localhost:9092", props.getProperty("bootstrap.servers"));
        assertEquals("test-client", props.getProperty("client.id"));
    }
    
    @Test
    @DisplayName("Test validate passes with bootstrap servers")
    public void testValidateWithBootstrapServers() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        properties.bootstrapServers("localhost:9092,localhost:9093");
        
        // When/Then
        assertDoesNotThrow(() -> properties.validate());
    }
    
    @Test
    @DisplayName("Test validate throws exception without bootstrap servers")
    public void testValidateWithoutBootstrapServers() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("bootstrap.servers must be configured", exception.getMessage());
    }
    
    @Test
    @DisplayName("Test validate throws exception with empty bootstrap servers")
    public void testValidateWithEmptyBootstrapServers() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        properties.bootstrapServers("");
        
        // When/Then
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> properties.validate());
        assertEquals("bootstrap.servers cannot be empty", exception.getMessage());
    }
    
    @Test
    @DisplayName("Test method chaining")
    public void testMethodChaining() {
        // Given
        CommonKafkaProperties properties = new CommonKafkaProperties();
        
        // When
        properties
            .bootstrapServers("localhost:9092")
            .clientId("test-client")
            .securityProtocol("SSL")
            .saslMechanism("PLAIN")
            .saslJaasConfig("config")
            .property("custom", "value");
        
        // Then
        Map<String, Object> map = properties.asMap();
        assertEquals("localhost:9092", map.get("bootstrap.servers"));
        assertEquals("test-client", map.get("client.id"));
        assertEquals("SSL", map.get("security.protocol"));
        assertEquals("PLAIN", map.get("sasl.mechanism"));
        assertEquals("config", map.get("sasl.jaas.config"));
        assertEquals("value", map.get("custom"));
    }
}