package com.manus.kafka.core.config;

import java.util.Map;
import java.util.Properties;

/**
 * Kafka Producer properties configuration.
 * This class extends CommonKafkaProperties and adds producer-specific properties.
 */
public class KafkaProducerProperties extends CommonKafkaProperties {
    
    /**
     * Creates a new instance of KafkaProducerProperties with default values.
     * Default values follow Kafka best practices.
     */
    public KafkaProducerProperties() {
        super();
        // Set default values for producer
        property("acks", "all"); // Ensure durability by default
        property("retries", 3);  // Retry a few times by default
        property("batch.size", 16384); // Default batch size
        property("linger.ms", 5); // Wait a bit to allow batching
        property("buffer.memory", 33554432); // 32MB buffer
    }
    
    /**
     * Sets the acknowledgment mode.
     * 
     * @param acks the acknowledgment mode (0, 1, all)
     * @return this instance for method chaining
     */
    public KafkaProducerProperties acks(String acks) {
        property("acks", acks);
        return this;
    }
    
    /**
     * Sets the number of retries.
     * 
     * @param retries the number of retries
     * @return this instance for method chaining
     */
    public KafkaProducerProperties retries(int retries) {
        property("retries", retries);
        return this;
    }
    
    /**
     * Sets the batch size in bytes.
     * 
     * @param batchSize the batch size in bytes
     * @return this instance for method chaining
     */
    public KafkaProducerProperties batchSize(int batchSize) {
        property("batch.size", batchSize);
        return this;
    }
    
    /**
     * Sets the linger time in milliseconds.
     * 
     * @param lingerMs the linger time in milliseconds
     * @return this instance for method chaining
     */
    public KafkaProducerProperties lingerMs(long lingerMs) {
        property("linger.ms", lingerMs);
        return this;
    }
    
    /**
     * Sets the buffer memory in bytes.
     * 
     * @param bufferMemory the buffer memory in bytes
     * @return this instance for method chaining
     */
    public KafkaProducerProperties bufferMemory(long bufferMemory) {
        property("buffer.memory", bufferMemory);
        return this;
    }
    
    /**
     * Sets the key serializer class.
     * 
     * @param keySerializerClass the fully qualified class name of the key serializer
     * @return this instance for method chaining
     */
    public KafkaProducerProperties keySerializer(String keySerializerClass) {
        property("key.serializer", keySerializerClass);
        return this;
    }
    
    /**
     * Sets the value serializer class.
     * 
     * @param valueSerializerClass the fully qualified class name of the value serializer
     * @return this instance for method chaining
     */
    public KafkaProducerProperties valueSerializer(String valueSerializerClass) {
        property("value.serializer", valueSerializerClass);
        return this;
    }
    
    /**
     * Sets the compression type.
     * 
     * @param compressionType the compression type (none, gzip, snappy, lz4, zstd)
     * @return this instance for method chaining
     */
    public KafkaProducerProperties compressionType(String compressionType) {
        property("compression.type", compressionType);
        return this;
    }
    
    /**
     * Sets the max request size in bytes.
     * 
     * @param maxRequestSize the max request size in bytes
     * @return this instance for method chaining
     */
    public KafkaProducerProperties maxRequestSize(int maxRequestSize) {
        property("max.request.size", maxRequestSize);
        return this;
    }
    
    /**
     * Sets the request timeout in milliseconds.
     * 
     * @param requestTimeoutMs the request timeout in milliseconds
     * @return this instance for method chaining
     */
    public KafkaProducerProperties requestTimeoutMs(int requestTimeoutMs) {
        property("request.timeout.ms", requestTimeoutMs);
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
    public KafkaProducerProperties property(String key, Object value) {
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
    public KafkaProducerProperties properties(Map<String, Object> props) {
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
        
        // Check for required serializers
        if (!props.containsKey("key.serializer")) {
            throw new IllegalStateException("key.serializer must be configured");
        }
        if (!props.containsKey("value.serializer")) {
            throw new IllegalStateException("value.serializer must be configured");
        }
        
        // Warn about potential issues
        if ("0".equals(props.get("acks"))) {
            System.out.println("WARNING: acks=0 provides no guarantee that records have been received by the broker. This may result in data loss.");
        }
        
        if (Integer.valueOf(0).equals(props.get("retries"))) {
            System.out.println("WARNING: retries=0 means no retries will be performed. This may result in message loss on transient errors.");
        }
    }
}