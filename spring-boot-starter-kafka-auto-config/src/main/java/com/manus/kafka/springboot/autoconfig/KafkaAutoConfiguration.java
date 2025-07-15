package com.manus.kafka.springboot.autoconfig;

import com.manus.kafka.core.config.KafkaAdminClientProperties;
import com.manus.kafka.core.config.KafkaConsumerProperties;
import com.manus.kafka.core.config.KafkaProducerProperties;
import com.manus.kafka.core.factory.KafkaAdminClientFactory;
import com.manus.kafka.core.factory.KafkaConsumerFactory;
import com.manus.kafka.core.factory.KafkaProducerFactory;
import com.manus.kafka.springboot.properties.KafkaProperties;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Auto-configuration for Kafka.
 */
@Configuration
@ConditionalOnClass(KafkaTemplate.class)
@EnableConfigurationProperties(KafkaProperties.class)
public class KafkaAutoConfiguration {
    
    private final KafkaProperties properties;
    
    public KafkaAutoConfiguration(KafkaProperties properties) {
        this.properties = properties;
    }
    
    /**
     * Creates a KafkaProducerProperties bean from the Spring Boot properties.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaProducerProperties kafkaProducerProperties() {
        KafkaProducerProperties producerProperties = getKafkaProducerProperties();

        if (properties.getProducer().getKeySerializer() != null) {
            producerProperties.keySerializer(properties.getProducer().getKeySerializer());
        } else {
            producerProperties.keySerializer(StringSerializer.class.getName());
        }
        
        if (properties.getProducer().getValueSerializer() != null) {
            producerProperties.valueSerializer(properties.getProducer().getValueSerializer());
        } else {
            producerProperties.valueSerializer(StringSerializer.class.getName());
        }
        
        if (properties.getProducer().getCompressionType() != null) {
            producerProperties.compressionType(properties.getProducer().getCompressionType());
        }
        
        // Add common properties
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            producerProperties.property(entry.getKey(), entry.getValue());
        }
        
        // Add producer-specific properties
        for (Map.Entry<String, String> entry : properties.getProducer().getProperties().entrySet()) {
            producerProperties.property(entry.getKey(), entry.getValue());
        }
        
        return producerProperties;
    }

    private KafkaProducerProperties getKafkaProducerProperties() {
        KafkaProducerProperties producerProperties = new KafkaProducerProperties();

        // Set common properties
        if (properties.getBootstrapServers() != null) {
            producerProperties.bootstrapServers(properties.getBootstrapServers());
        }
        if (properties.getClientId() != null) {
            producerProperties.clientId(properties.getClientId());
        }

        // Set security properties
        if (properties.getSecurity().getProtocol() != null) {
            producerProperties.securityProtocol(properties.getSecurity().getProtocol());
        }
        if (properties.getSecurity().getSaslMechanism() != null) {
            producerProperties.saslMechanism(properties.getSecurity().getSaslMechanism());
        }
        if (properties.getSecurity().getSaslJaasConfig() != null) {
            producerProperties.saslJaasConfig(properties.getSecurity().getSaslJaasConfig());
        }

        // Set producer-specific properties
        producerProperties.acks(properties.getProducer().getAcks());
        producerProperties.retries(properties.getProducer().getRetries());
        producerProperties.batchSize(properties.getProducer().getBatchSize());
        producerProperties.lingerMs(properties.getProducer().getLingerMs());
        producerProperties.bufferMemory(properties.getProducer().getBufferMemory());
        return producerProperties;
    }

    /**
     * Creates a KafkaConsumerProperties bean from the Spring Boot properties.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaConsumerProperties kafkaConsumerProperties() {
        KafkaConsumerProperties consumerProperties = new KafkaConsumerProperties();
        
        // Set common properties
        if (properties.getBootstrapServers() != null) {
            consumerProperties.bootstrapServers(properties.getBootstrapServers());
        }
        if (properties.getClientId() != null) {
            consumerProperties.clientId(properties.getClientId());
        }
        
        // Set security properties
        if (properties.getSecurity().getProtocol() != null) {
            consumerProperties.securityProtocol(properties.getSecurity().getProtocol());
        }
        if (properties.getSecurity().getSaslMechanism() != null) {
            consumerProperties.saslMechanism(properties.getSecurity().getSaslMechanism());
        }
        if (properties.getSecurity().getSaslJaasConfig() != null) {
            consumerProperties.saslJaasConfig(properties.getSecurity().getSaslJaasConfig());
        }
        
        // Set consumer-specific properties
        if (properties.getConsumer().getGroupId() != null) {
            consumerProperties.groupId(properties.getConsumer().getGroupId());
        }
        consumerProperties.enableAutoCommit(properties.getConsumer().getEnableAutoCommit());
        if (properties.getConsumer().getAutoCommitIntervalMs() != null) {
            consumerProperties.autoCommitIntervalMs(properties.getConsumer().getAutoCommitIntervalMs());
        }
        consumerProperties.autoOffsetReset(properties.getConsumer().getAutoOffsetReset());
        consumerProperties.maxPollRecords(properties.getConsumer().getMaxPollRecords());
        
        if (properties.getConsumer().getKeyDeserializer() != null) {
            consumerProperties.keyDeserializer(properties.getConsumer().getKeyDeserializer());
        } else {
            consumerProperties.keyDeserializer(StringDeserializer.class.getName());
        }
        
        if (properties.getConsumer().getValueDeserializer() != null) {
            consumerProperties.valueDeserializer(properties.getConsumer().getValueDeserializer());
        } else {
            consumerProperties.valueDeserializer(StringDeserializer.class.getName());
        }
        
        // Add common properties
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            consumerProperties.property(entry.getKey(), entry.getValue());
        }
        
        // Add consumer-specific properties
        for (Map.Entry<String, String> entry : properties.getConsumer().getProperties().entrySet()) {
            consumerProperties.property(entry.getKey(), entry.getValue());
        }
        
        return consumerProperties;
    }
    
    /**
     * Creates a KafkaAdminClientProperties bean from the Spring Boot properties.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaAdminClientProperties kafkaAdminClientProperties() {
        KafkaAdminClientProperties adminProperties = new KafkaAdminClientProperties();
        
        // Set common properties
        if (properties.getBootstrapServers() != null) {
            adminProperties.bootstrapServers(properties.getBootstrapServers());
        }
        if (properties.getClientId() != null) {
            adminProperties.clientId(properties.getClientId());
        }
        
        // Set security properties
        if (properties.getSecurity().getProtocol() != null) {
            adminProperties.securityProtocol(properties.getSecurity().getProtocol());
        }
        if (properties.getSecurity().getSaslMechanism() != null) {
            adminProperties.saslMechanism(properties.getSecurity().getSaslMechanism());
        }
        if (properties.getSecurity().getSaslJaasConfig() != null) {
            adminProperties.saslJaasConfig(properties.getSecurity().getSaslJaasConfig());
        }
        
        // Add common properties
        for (Map.Entry<String, String> entry : properties.getProperties().entrySet()) {
            adminProperties.property(entry.getKey(), entry.getValue());
        }
        
        // Add admin-specific properties
        for (Map.Entry<String, String> entry : properties.getAdmin().getProperties().entrySet()) {
            adminProperties.property(entry.getKey(), entry.getValue());
        }
        
        return adminProperties;
    }
    
    /**
     * Creates a KafkaProducerFactory bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaProducerFactory<?, ?> kafkaProducerFactory(KafkaProducerProperties producerProperties) {
        return new KafkaProducerFactory<>(producerProperties);
    }
    
    /**
     * Creates a KafkaConsumerFactory bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaConsumerFactory<?, ?> kafkaConsumerFactory(KafkaConsumerProperties consumerProperties) {
        return new KafkaConsumerFactory<>(consumerProperties);
    }
    
    /**
     * Creates a KafkaAdminClientFactory bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaAdminClientFactory kafkaAdminClientFactory(KafkaAdminClientProperties adminProperties) {
        return new KafkaAdminClientFactory(adminProperties);
    }
    
    /**
     * Creates a Spring KafkaAdmin bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaAdmin kafkaAdmin(KafkaAdminClientProperties adminProperties) {
        Map<String, Object> configs = adminProperties.asMap();
        return new KafkaAdmin(configs);
    }
    
    /**
     * Creates an AdminClient bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public AdminClient adminClient(KafkaAdminClientFactory adminClientFactory) {
        return adminClientFactory.createAdminClient();
    }
    
    /**
     * Creates a Spring DefaultKafkaProducerFactory bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultKafkaProducerFactory<?, ?> springKafkaProducerFactory(KafkaProducerProperties producerProperties) {
        return new DefaultKafkaProducerFactory<>(producerProperties.asMap());
    }
    
    /**
     * Creates a Spring DefaultKafkaConsumerFactory bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public DefaultKafkaConsumerFactory<?, ?> springKafkaConsumerFactory(KafkaConsumerProperties consumerProperties) {
        return new DefaultKafkaConsumerFactory<>(consumerProperties.asMap());
    }
    
    /**
     * Creates a KafkaTemplate bean.
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<?, ?> kafkaTemplate(DefaultKafkaProducerFactory<?, ?> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}