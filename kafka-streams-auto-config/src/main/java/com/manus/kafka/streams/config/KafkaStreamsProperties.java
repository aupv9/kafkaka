package com.manus.kafka.streams.config;

import com.manus.kafka.core.config.CommonKafkaProperties;

import java.util.Map;
import java.util.Properties;

/**
 * Kafka Streams properties.
 * This class provides a fluent API for configuring Kafka Streams properties.
 */
public class KafkaStreamsProperties extends CommonKafkaProperties {
    
    /**
     * Creates a new instance of KafkaStreamsProperties with default values.
     */
    public KafkaStreamsProperties() {
        // Default values can be set here
    }
    
    /**
     * Sets the application ID.
     * 
     * @param applicationId the application ID
     * @return this instance for method chaining
     */
    public KafkaStreamsProperties applicationId(String applicationId) {
        property("application.id", applicationId);
        return this;
    }
    
    /**
     * Sets the state directory.
     * 
     * @param stateDir the state directory
     * @return this instance for method chaining
     */
    public KafkaStreamsProperties stateDir(String stateDir) {
        property("state.dir", stateDir);
        return this;
    }
    
    /**
     * Sets the number of stream threads.
     * 
     * @param numStreamThreads the number of stream threads
     * @return this instance for method chaining
     */
    public KafkaStreamsProperties numStreamThreads(int numStreamThreads) {
        property("num.stream.threads", numStreamThreads);
        return this;
    }
    
    /**
     * Sets the replication factor for changelog topics and repartition topics.
     * 
     * @param replicationFactor the replication factor
     * @return this instance for method chaining
     */
    public KafkaStreamsProperties replicationFactor(int replicationFactor) {
        property("replication.factor", replicationFactor);
        return this;
    }
    
    /**
     * Sets the default key serde class.
     * 
     * @param defaultKeySerde the default key serde class name
     * @return this instance for method chaining
     */
    public KafkaStreamsProperties defaultKeySerde(String defaultKeySerde) {
        property("default.key.serde", defaultKeySerde);
        return this;
    }
    
    /**
     * Sets the default value serde class.
     * 
     * @param defaultValueSerde the default value serde class name
     * @return this instance for method chaining
     */
    public KafkaStreamsProperties defaultValueSerde(String defaultValueSerde) {
        property("default.value.serde", defaultValueSerde);
        return this;
    }
    
    /**
     * Sets the commit interval ms.
     * 
     * @param commitIntervalMs the commit interval in milliseconds
     * @return this instance for method chaining
     */
    public KafkaStreamsProperties commitIntervalMs(long commitIntervalMs) {
        property("commit.interval.ms", commitIntervalMs);
        return this;
    }
    
    /**
     * Sets the processing guarantee.
     * 
     * @param processingGuarantee the processing guarantee (at_least_once, exactly_once, exactly_once_v2)
     * @return this instance for method chaining
     */
    public KafkaStreamsProperties processingGuarantee(String processingGuarantee) {
        property("processing.guarantee", processingGuarantee);
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
    public KafkaStreamsProperties property(String key, Object value) {
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
    public KafkaStreamsProperties properties(Map<String, Object> props) {
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
        
        if (!asMap().containsKey("application.id")) {
            throw new IllegalStateException("application.id must be configured");
        }
        
        String applicationId = (String) asMap().get("application.id");
        if (applicationId == null || applicationId.isEmpty()) {
            throw new IllegalStateException("application.id cannot be empty");
        }
    }
}