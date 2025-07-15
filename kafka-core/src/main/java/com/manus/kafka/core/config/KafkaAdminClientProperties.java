package com.manus.kafka.core.config;

import java.util.Map;
import java.util.Properties;

/**
 * Kafka AdminClient properties configuration.
 * This class extends CommonKafkaProperties and adds admin client-specific properties.
 */
public class KafkaAdminClientProperties extends CommonKafkaProperties {
    
    /**
     * Creates a new instance of KafkaAdminClientProperties with default values.
     * Default values follow Kafka best practices.
     */
    public KafkaAdminClientProperties() {
        super();
        // Set default values for admin client
        property("request.timeout.ms", 30000); // 30 seconds default timeout
        property("retries", 5); // Retry a few times by default
    }
    
    /**
     * Sets the request timeout in milliseconds.
     * 
     * @param requestTimeoutMs the request timeout in milliseconds
     * @return this instance for method chaining
     */
    public KafkaAdminClientProperties requestTimeoutMs(int requestTimeoutMs) {
        property("request.timeout.ms", requestTimeoutMs);
        return this;
    }
    
    /**
     * Sets the number of retries.
     * 
     * @param retries the number of retries
     * @return this instance for method chaining
     */
    public KafkaAdminClientProperties retries(int retries) {
        property("retries", retries);
        return this;
    }
    
    /**
     * Sets the retry backoff in milliseconds.
     * 
     * @param retryBackoffMs the retry backoff in milliseconds
     * @return this instance for method chaining
     */
    public KafkaAdminClientProperties retryBackoffMs(long retryBackoffMs) {
        property("retry.backoff.ms", retryBackoffMs);
        return this;
    }
    
    /**
     * Sets the connections max idle time in milliseconds.
     * 
     * @param connectionsMaxIdleMs the connections max idle time in milliseconds
     * @return this instance for method chaining
     */
    public KafkaAdminClientProperties connectionsMaxIdleMs(long connectionsMaxIdleMs) {
        property("connections.max.idle.ms", connectionsMaxIdleMs);
        return this;
    }
    
    /**
     * Sets a custom property.
     * 
     * @param key the property key
     * @param value the property value
     * @return this instance for method chaining
     */
    @Override
    public KafkaAdminClientProperties property(String key, Object value) {
        super.property(key, value);
        return this;
    }
    
    /**
     * Sets multiple custom properties.
     * 
     * @param props the properties to set
     * @return this instance for method chaining
     */
    @Override
    public KafkaAdminClientProperties properties(Map<String, Object> props) {
        super.properties(props);
        return this;
    }
    
    /**
     * Validates the properties.
     * 
     * @throws IllegalStateException if the properties are invalid
     */
    @Override
    public void validate() {
        super.validate();
        
        Map<String, Object> props = asMap();
        
        // Warn about potential issues
        if (Integer.valueOf(0).equals(props.get("retries"))) {
            System.out.println("WARNING: retries=0 means no retries will be performed. This may result in failures on transient errors.");
        }
        
        Integer requestTimeoutMs = (Integer) props.get("request.timeout.ms");
        if (requestTimeoutMs != null && requestTimeoutMs < 5000) {
            System.out.println("WARNING: request.timeout.ms is set to a low value (" + requestTimeoutMs + "ms). This may cause operations to time out prematurely.");
        }
    }
}