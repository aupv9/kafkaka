package com.manus.kafka.streams.springboot.autoconfig;

import com.manus.kafka.streams.config.KafkaStreamsProperties;
import com.manus.kafka.streams.factory.KafkaStreamsFactory;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Auto-configuration for Kafka Streams.
 */
@Configuration
@ConditionalOnClass(StreamsBuilder.class)
@EnableConfigurationProperties(com.manus.kafka.streams.springboot.properties.KafkaStreamsProperties.class)
@ConditionalOnProperty(prefix = "spring.kafka.streams", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaStreamsAutoConfiguration {
    
    private final com.manus.kafka.streams.springboot.properties.KafkaStreamsProperties properties;
    
    public KafkaStreamsAutoConfiguration(com.manus.kafka.streams.springboot.properties.KafkaStreamsProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Creates a KafkaStreamsProperties bean from the Spring Boot properties.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaStreamsProperties kafkaStreamsProperties() {
        KafkaStreamsProperties streamsProperties = new KafkaStreamsProperties();
        
        // Set common properties from parent Kafka properties
        // This would typically come from the parent KafkaProperties
        // For simplicity, we'll assume these are set in the streams properties
        
        // Set Kafka Streams specific properties
        if (properties.getApplicationId() != null) {
            streamsProperties.applicationId(properties.getApplicationId());
        }
        
        if (properties.getStateDir() != null) {
            streamsProperties.stateDir(properties.getStateDir());
        }
        
        if (properties.getNumStreamThreads() != null) {
            streamsProperties.numStreamThreads(properties.getNumStreamThreads());
        }
        
        if (properties.getReplicationFactor() != null) {
            streamsProperties.replicationFactor(properties.getReplicationFactor());
        }
        
        if (properties.getDefaultKeySerde() != null) {
            streamsProperties.defaultKeySerde(properties.getDefaultKeySerde());
        } else {
            streamsProperties.defaultKeySerde(Serdes.String().getClass().getName());
        }
        
        if (properties.getDefaultValueSerde() != null) {
            streamsProperties.defaultValueSerde(properties.getDefaultValueSerde());
        } else {
            streamsProperties.defaultValueSerde(Serdes.String().getClass().getName());
        }
        
        if (properties.getCommitIntervalMs() != null) {
            streamsProperties.commitIntervalMs(properties.getCommitIntervalMs());
        }
        
        if (properties.getProcessingGuarantee() != null) {
            streamsProperties.processingGuarantee(properties.getProcessingGuarantee());
        }
        
        // Add additional properties
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            streamsProperties.property(entry.getKey(), entry.getValue());
        }
        
        return streamsProperties;
    }
    
    /**
     * Creates a KafkaStreamsFactory bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaStreamsFactory kafkaStreamsFactory(KafkaStreamsProperties streamsProperties) {
        return new KafkaStreamsFactory(streamsProperties);
    }
    
    /**
     * Creates a StreamsBuilder bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public StreamsBuilder streamsBuilder() {
        return new StreamsBuilder();
    }
    
    /**
     * Creates a KafkaStreamsConfiguration bean for Spring Kafka integration.
     */
    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    @ConditionalOnMissingBean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public KafkaStreamsConfiguration kafkaStreamsConfiguration(KafkaStreamsProperties streamsProperties) {
        Map<String, Object> props = streamsProperties.asMap();
        return new KafkaStreamsConfiguration(props);
    }
    
    /**
     * Creates a StreamsBuilderFactoryBean for Spring Kafka integration.
     */
    @Bean
    @ConditionalOnMissingBean
    public StreamsBuilderFactoryBean streamsBuilderFactoryBean(KafkaStreamsConfiguration streamsConfig) {
        return new StreamsBuilderFactoryBean(streamsConfig);
    }
}