package com.manus.kafka.streams.springboot.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration properties for Kafka Streams.
 * This class maps Spring Boot configuration properties to Kafka Streams properties.
 */
@ConfigurationProperties(prefix = "spring.kafka.streams")
public class KafkaStreamsProperties {
    
    /**
     * Application ID for the Kafka Streams application.
     * This is required for all Kafka Streams applications.
     */
    private String applicationId;
    
    /**
     * Directory location for the state store.
     */
    private String stateDir;
    
    /**
     * Number of threads to execute stream processing.
     */
    private Integer numStreamThreads = 1;
    
    /**
     * Replication factor for changelog topics and repartition topics.
     */
    private Integer replicationFactor;
    
    /**
     * Default serializer/deserializer class for key.
     */
    private String defaultKeySerde;
    
    /**
     * Default serializer/deserializer class for value.
     */
    private String defaultValueSerde;
    
    /**
     * The frequency with which to save the position of the processor.
     */
    private Long commitIntervalMs;
    
    /**
     * Processing guarantee that should be used.
     * Possible values: at_least_once, exactly_once, exactly_once_v2.
     */
    private String processingGuarantee;
    
    /**
     * Additional properties specific to Kafka Streams.
     */
    private Map<String, String> properties = new HashMap<>();
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getStateDir() {
        return stateDir;
    }
    
    public void setStateDir(String stateDir) {
        this.stateDir = stateDir;
    }
    
    public Integer getNumStreamThreads() {
        return numStreamThreads;
    }
    
    public void setNumStreamThreads(Integer numStreamThreads) {
        this.numStreamThreads = numStreamThreads;
    }
    
    public Integer getReplicationFactor() {
        return replicationFactor;
    }
    
    public void setReplicationFactor(Integer replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
    
    public String getDefaultKeySerde() {
        return defaultKeySerde;
    }
    
    public void setDefaultKeySerde(String defaultKeySerde) {
        this.defaultKeySerde = defaultKeySerde;
    }
    
    public String getDefaultValueSerde() {
        return defaultValueSerde;
    }
    
    public void setDefaultValueSerde(String defaultValueSerde) {
        this.defaultValueSerde = defaultValueSerde;
    }
    
    public Long getCommitIntervalMs() {
        return commitIntervalMs;
    }
    
    public void setCommitIntervalMs(Long commitIntervalMs) {
        this.commitIntervalMs = commitIntervalMs;
    }
    
    public String getProcessingGuarantee() {
        return processingGuarantee;
    }
    
    public void setProcessingGuarantee(String processingGuarantee) {
        this.processingGuarantee = processingGuarantee;
    }
    
    public Map<String, String> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}