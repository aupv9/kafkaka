package com.manus.kafka.core.config;

import java.util.Map;
import java.util.Properties;

/**
 * Kafka Consumer properties configuration.
 * This class extends CommonKafkaProperties and adds consumer-specific properties.
 */
public class KafkaConsumerProperties extends CommonKafkaProperties {
    
    /**
     * Creates a new instance of KafkaConsumerProperties with default values.
     * Default values follow Kafka best practices.
     */
    public KafkaConsumerProperties() {
        super();
        // Set default values for consumer
        property("enable.auto.commit", "false"); // Manual commit by default for better control
        property("auto.offset.reset", "earliest"); // Start from the beginning by default
        property("fetch.min.bytes", 1); // Default fetch min bytes
        property("fetch.max.wait.ms", 500); // Default fetch max wait time
        property("max.poll.records", 500); // Default max poll records
    }
    
    /**
     * Sets the group ID.
     * 
     * @param groupId the consumer group ID
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties groupId(String groupId) {
        property("group.id", groupId);
        return this;
    }
    
    /**
     * Sets whether to enable auto commit.
     * 
     * @param enableAutoCommit whether to enable auto commit
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties enableAutoCommit(boolean enableAutoCommit) {
        property("enable.auto.commit", String.valueOf(enableAutoCommit));
        return this;
    }
    
    /**
     * Sets the auto commit interval in milliseconds.
     * 
     * @param autoCommitIntervalMs the auto commit interval in milliseconds
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties autoCommitIntervalMs(int autoCommitIntervalMs) {
        property("auto.commit.interval.ms", autoCommitIntervalMs);
        return this;
    }
    
    /**
     * Sets the auto offset reset policy.
     * 
     * @param autoOffsetReset the auto offset reset policy (earliest, latest, none)
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties autoOffsetReset(String autoOffsetReset) {
        property("auto.offset.reset", autoOffsetReset);
        return this;
    }
    
    /**
     * Sets the fetch minimum bytes.
     * 
     * @param fetchMinBytes the fetch minimum bytes
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties fetchMinBytes(int fetchMinBytes) {
        property("fetch.min.bytes", fetchMinBytes);
        return this;
    }
    
    /**
     * Sets the fetch maximum wait time in milliseconds.
     * 
     * @param fetchMaxWaitMs the fetch maximum wait time in milliseconds
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties fetchMaxWaitMs(int fetchMaxWaitMs) {
        property("fetch.max.wait.ms", fetchMaxWaitMs);
        return this;
    }
    
    /**
     * Sets the maximum number of records returned in a single call to poll().
     * 
     * @param maxPollRecords the maximum number of records
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties maxPollRecords(int maxPollRecords) {
        property("max.poll.records", maxPollRecords);
        return this;
    }
    
    /**
     * Sets the maximum amount of time in milliseconds that the consumer can be idle before the broker
     * considers it failed.
     * 
     * @param sessionTimeoutMs the session timeout in milliseconds
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties sessionTimeoutMs(int sessionTimeoutMs) {
        property("session.timeout.ms", sessionTimeoutMs);
        return this;
    }
    
    /**
     * Sets the maximum delay between invocations of poll() when using consumer group management.
     * 
     * @param maxPollIntervalMs the maximum poll interval in milliseconds
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties maxPollIntervalMs(int maxPollIntervalMs) {
        property("max.poll.interval.ms", maxPollIntervalMs);
        return this;
    }
    
    /**
     * Sets the key deserializer class.
     * 
     * @param keyDeserializerClass the fully qualified class name of the key deserializer
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties keyDeserializer(String keyDeserializerClass) {
        property("key.deserializer", keyDeserializerClass);
        return this;
    }
    
    /**
     * Sets the value deserializer class.
     * 
     * @param valueDeserializerClass the fully qualified class name of the value deserializer
     * @return this instance for method chaining
     */
    public KafkaConsumerProperties valueDeserializer(String valueDeserializerClass) {
        property("value.deserializer", valueDeserializerClass);
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
    public KafkaConsumerProperties property(String key, Object value) {
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
    public KafkaConsumerProperties properties(Map<String, Object> props) {
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
        
        // Check for required properties
        if (!props.containsKey("group.id")) {
            throw new IllegalStateException("group.id must be configured");
        }
        
        // Check for required deserializers
        if (!props.containsKey("key.deserializer")) {
            throw new IllegalStateException("key.deserializer must be configured");
        }
        if (!props.containsKey("value.deserializer")) {
            throw new IllegalStateException("value.deserializer must be configured");
        }
        
        // Warn about potential issues
        if ("true".equals(props.get("enable.auto.commit"))) {
            System.out.println("WARNING: enable.auto.commit=true may result in duplicate processing or message loss in case of errors. Consider using manual commit for better control.");
        }
        
        if ("latest".equals(props.get("auto.offset.reset"))) {
            System.out.println("WARNING: auto.offset.reset=latest will cause the consumer to miss messages that were sent while it was offline.");
        }
    }
}