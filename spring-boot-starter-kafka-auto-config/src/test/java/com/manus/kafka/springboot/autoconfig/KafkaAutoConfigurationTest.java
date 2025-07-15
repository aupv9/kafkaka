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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link KafkaAutoConfiguration}.
 */
@ExtendWith(MockitoExtension.class)
public class KafkaAutoConfigurationTest {

    @Mock
    private Producer<Object, Object> mockProducer;

    @Mock
    private Consumer<Object, Object> mockConsumer;

    @Mock
    private AdminClient mockAdminClient;

    @Test
    @DisplayName("Test kafkaProducerProperties creates properties with correct values")
    public void testKafkaProducerProperties() {
        // Given
        KafkaProperties properties = new KafkaProperties();
        properties.setBootstrapServers("localhost:9092");
        properties.setClientId("test-client");

        Map<String, String> commonProps = new HashMap<>();
        commonProps.put("common.prop", "common-value");
        properties.setProperties(commonProps);

        properties.getProducer().setKeySerializer("org.apache.kafka.common.serialization.StringSerializer");
        properties.getProducer().setValueSerializer("org.apache.kafka.common.serialization.StringSerializer");
        properties.getProducer().setCompressionType("gzip");

        Map<String, String> producerProps = new HashMap<>();
        producerProps.put("producer.prop", "producer-value");
        properties.getProducer().setProperties(producerProps);

        properties.getSecurity().setProtocol("SASL_SSL");
        properties.getSecurity().setSaslMechanism("PLAIN");
        properties.getSecurity().setSaslJaasConfig("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");

        KafkaAutoConfiguration autoConfig = new KafkaAutoConfiguration(properties);

        // When
        KafkaProducerProperties producerProperties = autoConfig.kafkaProducerProperties();

        // Then
        Map<String, Object> map = producerProperties.asMap();
        assertEquals("localhost:9092", map.get("bootstrap.servers"));
        assertEquals("test-client", map.get("client.id"));
        assertEquals("common-value", map.get("common.prop"));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", map.get("key.serializer"));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", map.get("value.serializer"));
        assertEquals("gzip", map.get("compression.type"));
        assertEquals("producer-value", map.get("producer.prop"));
        assertEquals("SASL_SSL", map.get("security.protocol"));
        assertEquals("PLAIN", map.get("sasl.mechanism"));
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";", 
                     map.get("sasl.jaas.config"));
    }

    @Test
    @DisplayName("Test kafkaConsumerProperties creates properties with correct values")
    public void testKafkaConsumerProperties() {
        // Given
        KafkaProperties properties = new KafkaProperties();
        properties.setBootstrapServers("localhost:9092");
        properties.setClientId("test-client");

        Map<String, String> commonProps = new HashMap<>();
        commonProps.put("common.prop", "common-value");
        properties.setProperties(commonProps);

        properties.getConsumer().setGroupId("test-group");
        properties.getConsumer().setEnableAutoCommit(true);
        properties.getConsumer().setAutoCommitIntervalMs(5000);
        properties.getConsumer().setAutoOffsetReset("latest");
        properties.getConsumer().setKeyDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        properties.getConsumer().setValueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
        properties.getConsumer().setMaxPollRecords(1000);

        Map<String, String> consumerProps = new HashMap<>();
        consumerProps.put("consumer.prop", "consumer-value");
        properties.getConsumer().setProperties(consumerProps);

        properties.getSecurity().setProtocol("SASL_SSL");
        properties.getSecurity().setSaslMechanism("PLAIN");
        properties.getSecurity().setSaslJaasConfig("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");

        KafkaAutoConfiguration autoConfig = new KafkaAutoConfiguration(properties);

        // When
        KafkaConsumerProperties consumerProperties = autoConfig.kafkaConsumerProperties();

        // Then
        Map<String, Object> map = consumerProperties.asMap();
        assertEquals("localhost:9092", map.get("bootstrap.servers"));
        assertEquals("test-client", map.get("client.id"));
        assertEquals("common-value", map.get("common.prop"));
        assertEquals("test-group", map.get("group.id"));
        assertEquals("true", map.get("enable.auto.commit").toString());
        assertEquals(5000, map.get("auto.commit.interval.ms"));
        assertEquals("latest", map.get("auto.offset.reset"));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", map.get("key.deserializer"));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", map.get("value.deserializer"));
        assertEquals(1000, map.get("max.poll.records"));
        assertEquals("consumer-value", map.get("consumer.prop"));
        assertEquals("SASL_SSL", map.get("security.protocol"));
        assertEquals("PLAIN", map.get("sasl.mechanism"));
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";", 
                     map.get("sasl.jaas.config"));
    }

    @Test
    @DisplayName("Test kafkaAdminClientProperties creates properties with correct values")
    public void testKafkaAdminClientProperties() {
        // Given
        KafkaProperties properties = new KafkaProperties();
        properties.setBootstrapServers("localhost:9092");
        properties.setClientId("test-client");

        Map<String, String> commonProps = new HashMap<>();
        commonProps.put("common.prop", "common-value");
        properties.setProperties(commonProps);

        Map<String, String> adminProps = new HashMap<>();
        adminProps.put("admin.prop", "admin-value");
        properties.getAdmin().setProperties(adminProps);

        properties.getSecurity().setProtocol("SASL_SSL");
        properties.getSecurity().setSaslMechanism("PLAIN");
        properties.getSecurity().setSaslJaasConfig("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");

        KafkaAutoConfiguration autoConfig = new KafkaAutoConfiguration(properties);

        // When
        KafkaAdminClientProperties adminProperties = autoConfig.kafkaAdminClientProperties();

        // Then
        Map<String, Object> map = adminProperties.asMap();
        assertEquals("localhost:9092", map.get("bootstrap.servers"));
        assertEquals("test-client", map.get("client.id"));
        assertEquals("common-value", map.get("common.prop"));
        assertEquals("admin-value", map.get("admin.prop"));
        assertEquals("SASL_SSL", map.get("security.protocol"));
        assertEquals("PLAIN", map.get("sasl.mechanism"));
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";", 
                     map.get("sasl.jaas.config"));
    }

    @Test
    @DisplayName("Test factory beans are created correctly")
    public void testFactoryBeans() {
        // Given
        KafkaProperties properties = new KafkaProperties();
        properties.setBootstrapServers("localhost:9092");
        KafkaAutoConfiguration autoConfig = new KafkaAutoConfiguration(properties);

        KafkaProducerProperties producerProperties = autoConfig.kafkaProducerProperties();
        KafkaConsumerProperties consumerProperties = autoConfig.kafkaConsumerProperties();
        KafkaAdminClientProperties adminProperties = autoConfig.kafkaAdminClientProperties();

        // When
        KafkaProducerFactory<?, ?> producerFactory = autoConfig.kafkaProducerFactory(producerProperties);
        KafkaConsumerFactory<?, ?> consumerFactory = autoConfig.kafkaConsumerFactory(consumerProperties);
        KafkaAdminClientFactory adminClientFactory = autoConfig.kafkaAdminClientFactory(adminProperties);

        // Then
        assertNotNull(producerFactory);
        assertNotNull(consumerFactory);
        assertNotNull(adminClientFactory);
    }

    @Test
    @DisplayName("Test Spring Kafka beans are created correctly")
    public void testSpringKafkaBeans() {
        // Given
        KafkaProperties properties = new KafkaProperties();
        properties.setBootstrapServers("localhost:9092");
        KafkaAutoConfiguration autoConfig = new KafkaAutoConfiguration(properties);

        KafkaProducerProperties producerProperties = autoConfig.kafkaProducerProperties();
        KafkaConsumerProperties consumerProperties = autoConfig.kafkaConsumerProperties();
        KafkaAdminClientProperties adminProperties = autoConfig.kafkaAdminClientProperties();

        // When
        DefaultKafkaProducerFactory<?, ?> springProducerFactory = autoConfig.springKafkaProducerFactory(producerProperties);
        DefaultKafkaConsumerFactory<?, ?> springConsumerFactory = autoConfig.springKafkaConsumerFactory(consumerProperties);
        KafkaAdmin kafkaAdmin = autoConfig.kafkaAdmin(adminProperties);
        KafkaTemplate<?, ?> kafkaTemplate = autoConfig.kafkaTemplate(springProducerFactory);

        // Then
        assertNotNull(springProducerFactory);
        assertNotNull(springConsumerFactory);
        assertNotNull(kafkaAdmin);
        assertNotNull(kafkaTemplate);

        // Verify that the properties were passed correctly
        Map<String, Object> adminConfigs = kafkaAdmin.getConfigurationProperties();
        assertEquals("localhost:9092", adminConfigs.get("bootstrap.servers"));
    }

    @Test
    @DisplayName("Test client beans are created correctly")
    public void testClientBeans() {
        // Given
        KafkaProperties properties = new KafkaProperties();
        properties.setBootstrapServers("localhost:9092");
        KafkaAutoConfiguration autoConfig = new KafkaAutoConfiguration(properties);

        KafkaAdminClientFactory adminClientFactory = mock(KafkaAdminClientFactory.class);
        when(adminClientFactory.createAdminClient()).thenReturn(mockAdminClient);

        // When
        AdminClient adminClient = autoConfig.adminClient(adminClientFactory);

        // Then
        assertSame(mockAdminClient, adminClient);
        verify(adminClientFactory).createAdminClient();
    }
}
