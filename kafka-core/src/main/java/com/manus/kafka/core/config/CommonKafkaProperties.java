package com.manus.kafka.core.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Common Kafka properties that apply to all client types (Producer, Consumer, AdminClient).
 * This class provides a fluent API for configuring common Kafka properties.
 */
public class CommonKafkaProperties {
    
    private final Map<String, Object> properties = new HashMap<>();
    
    /**
     * Creates a new instance of CommonKafkaProperties with default values.
     * Default values follow Kafka best practices.
     */
    public CommonKafkaProperties() {
        // Default values can be set here
    }
    
    /**
     * Sets the bootstrap servers.
     * 
     * @param bootstrapServers comma-separated list of host:port pairs
     * @return this instance for method chaining
     */
    public CommonKafkaProperties bootstrapServers(String bootstrapServers) {
        properties.put("bootstrap.servers", bootstrapServers);
        return this;
    }
    
    /**
     * Sets the client ID.
     * 
     * @param clientId the client ID
     * @return this instance for method chaining
     */
    public CommonKafkaProperties clientId(String clientId) {
        properties.put("client.id", clientId);
        return this;
    }
    
    /**
     * Sets the security protocol.
     * 
     * @param securityProtocol the security protocol (PLAINTEXT, SSL, SASL_PLAINTEXT, SASL_SSL)
     * @return this instance for method chaining
     */
    public CommonKafkaProperties securityProtocol(String securityProtocol) {
        properties.put("security.protocol", securityProtocol);
        return this;
    }
    
    /**
     * Sets the SASL mechanism.
     * 
     * @param saslMechanism the SASL mechanism (GSSAPI, PLAIN, SCRAM-SHA-256, SCRAM-SHA-512, OAUTHBEARER)
     * @return this instance for method chaining
     */
    public CommonKafkaProperties saslMechanism(String saslMechanism) {
        properties.put("sasl.mechanism", saslMechanism);
        return this;
    }
    
    /**
     * Sets the SASL JAAS config.
     * 
     * @param saslJaasConfig the SASL JAAS config
     * @return this instance for method chaining
     */
    public CommonKafkaProperties saslJaasConfig(String saslJaasConfig) {
        properties.put("sasl.jaas.config", saslJaasConfig);
        return this;
    }
    
    /**
     * Sets a custom property.
     * 
     * @param key the property key
     * @param value the property value
     * @return this instance for method chaining
     */
    public CommonKafkaProperties property(String key, Object value) {
        properties.put(key, value);
        return this;
    }
    
    /**
     * Sets multiple custom properties.
     * 
     * @param props the properties to set
     * @return this instance for method chaining
     */
    public CommonKafkaProperties properties(Map<String, Object> props) {
        properties.putAll(props);
        return this;
    }
    
    /**
     * Returns the properties as a Map.
     * 
     * @return the properties as a Map
     */
    public Map<String, Object> asMap() {
        return new HashMap<>(properties);
    }
    
    /**
     * Returns the properties as a Properties object.
     * 
     * @return the properties as a Properties object
     */
    public Properties asProperties() {
        Properties props = new Properties();
        props.putAll(properties);
        return props;
    }
    
    /**
     * Validates the properties.
     * 
     * @throws IllegalStateException if the properties are invalid
     */
    public void validate() {
        if (!properties.containsKey("bootstrap.servers")) {
            throw new IllegalStateException("bootstrap.servers must be configured");
        }
        
        String bootstrapServers = (String) properties.get("bootstrap.servers");
        if (bootstrapServers == null || bootstrapServers.isEmpty()) {
            throw new IllegalStateException("bootstrap.servers cannot be empty");
        }
        
        // Check if only one bootstrap server is configured (not recommended for production)
        if (!bootstrapServers.contains(",")) {
            System.out.println("WARNING: Only one bootstrap server is configured. This is not recommended for production environments.");
        }
    }
}