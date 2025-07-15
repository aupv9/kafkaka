package com.manus.kafka.j2ee.builder;

import com.manus.kafka.core.config.KafkaConsumerProperties;
import com.manus.kafka.core.factory.KafkaConsumerFactory;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedConstruction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link J2EEKafkaConsumerBuilder}.
 */
public class J2EEKafkaConsumerBuilderTest {

    private Consumer<String, String> mockConsumer;
    
    @BeforeEach
    public void setUp() {
        mockConsumer = mock(Consumer.class);
    }

    @Test
    @DisplayName("Test default constructor creates default properties")
    public void testDefaultConstructor() {
        // When
        J2EEKafkaConsumerBuilder<String, String> builder = new J2EEKafkaConsumerBuilder<>();
        
        // Then
        KafkaConsumerProperties properties = builder.getProperties();
        assertNotNull(properties);
        assertEquals(false, properties.asMap().get("enable.auto.commit"));
        assertEquals("earliest", properties.asMap().get("auto.offset.reset"));
    }

    @Test
    @DisplayName("Test constructor with properties")
    public void testConstructorWithProperties() {
        // Given
        KafkaConsumerProperties properties = new KafkaConsumerProperties();
        properties.bootstrapServers("localhost:9092");
        
        // When
        J2EEKafkaConsumerBuilder<String, String> builder = new J2EEKafkaConsumerBuilder<>(properties);
        
        // Then
        assertSame(properties, builder.getProperties());
        assertEquals("localhost:9092", properties.asMap().get("bootstrap.servers"));
    }

    @Test
    @DisplayName("Test builder methods set properties correctly")
    public void testBuilderMethods() {
        // When
        J2EEKafkaConsumerBuilder<String, String> builder = new J2EEKafkaConsumerBuilder<String, String>()
            .bootstrapServers("localhost:9092")
            .clientId("test-client")
            .groupId("test-group")
            .enableAutoCommit(true)
            .autoCommitIntervalMs(5000)
            .autoOffsetReset("latest")
            .fetchMinBytes(1024)
            .fetchMaxWaitMs(500)
            .maxPollRecords(1000)
            .keyDeserializer("org.apache.kafka.common.serialization.StringDeserializer")
            .valueDeserializer("org.apache.kafka.common.serialization.StringDeserializer")
            .securityProtocol("SASL_SSL")
            .saslMechanism("PLAIN")
            .saslJaasConfig("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");
        
        // Then
        KafkaConsumerProperties properties = builder.getProperties();
        Map<String, Object> map = properties.asMap();
        assertEquals("localhost:9092", map.get("bootstrap.servers"));
        assertEquals("test-client", map.get("client.id"));
        assertEquals("test-group", map.get("group.id"));
        assertEquals(true, map.get("enable.auto.commit"));
        assertEquals(5000, map.get("auto.commit.interval.ms"));
        assertEquals("latest", map.get("auto.offset.reset"));
        assertEquals(1024, map.get("fetch.min.bytes"));
        assertEquals(500, map.get("fetch.max.wait.ms"));
        assertEquals(1000, map.get("max.poll.records"));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", map.get("key.deserializer"));
        assertEquals("org.apache.kafka.common.serialization.StringDeserializer", map.get("value.deserializer"));
        assertEquals("SASL_SSL", map.get("security.protocol"));
        assertEquals("PLAIN", map.get("sasl.mechanism"));
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";", 
                     map.get("sasl.jaas.config"));
    }

    @Test
    @DisplayName("Test property method sets custom property")
    public void testPropertyMethod() {
        // When
        J2EEKafkaConsumerBuilder<String, String> builder = new J2EEKafkaConsumerBuilder<String, String>()
            .property("custom.property", "custom-value");
        
        // Then
        assertEquals("custom-value", builder.getProperties().asMap().get("custom.property"));
    }

    @Test
    @DisplayName("Test properties method sets multiple custom properties")
    public void testPropertiesMethod() {
        // Given
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("prop1", "value1");
        customProps.put("prop2", "value2");
        
        // When
        J2EEKafkaConsumerBuilder<String, String> builder = new J2EEKafkaConsumerBuilder<String, String>()
            .properties(customProps);
        
        // Then
        assertEquals("value1", builder.getProperties().asMap().get("prop1"));
        assertEquals("value2", builder.getProperties().asMap().get("prop2"));
    }

    @Test
    @DisplayName("Test build method creates consumer with string deserializers")
    public void testBuildWithStringDeserializers() {
        try (MockedConstruction<KafkaConsumerFactory> mocked = mockConstruction(
                KafkaConsumerFactory.class,
                (mock, context) -> {
                    when(mock.createConsumer()).thenReturn(mockConsumer);
                })) {
            
            // Given
            J2EEKafkaConsumerBuilder<String, String> builder = new J2EEKafkaConsumerBuilder<String, String>()
                .bootstrapServers("localhost:9092")
                .groupId("test-group")
                .keyDeserializer("org.apache.kafka.common.serialization.StringDeserializer")
                .valueDeserializer("org.apache.kafka.common.serialization.StringDeserializer");
            
            // When
            Consumer<String, String> consumer = builder.build();
            
            // Then
            assertSame(mockConsumer, consumer);
            assertEquals(1, mocked.constructed().size());
            KafkaConsumerFactory factory = mocked.constructed().get(0);
            verify(factory).createConsumer();
            verifyNoMoreInteractions(factory);
        }
    }

    @Test
    @DisplayName("Test build method creates consumer with deserializer instances")
    public void testBuildWithDeserializerInstances() {
        try (MockedConstruction<KafkaConsumerFactory> mocked = mockConstruction(
                KafkaConsumerFactory.class,
                (mock, context) -> {
                    when(mock.createConsumer(any(StringDeserializer.class), any(StringDeserializer.class)))
                        .thenReturn(mockConsumer);
                })) {
            
            // Given
            StringDeserializer keyDeserializer = new StringDeserializer();
            StringDeserializer valueDeserializer = new StringDeserializer();
            
            J2EEKafkaConsumerBuilder<String, String> builder = new J2EEKafkaConsumerBuilder<String, String>()
                .bootstrapServers("localhost:9092")
                .groupId("test-group")
                .keyDeserializer(keyDeserializer)
                .valueDeserializer(valueDeserializer);
            
            // When
            Consumer<String, String> consumer = builder.build();
            
            // Then
            assertSame(mockConsumer, consumer);
            assertEquals(1, mocked.constructed().size());
            KafkaConsumerFactory factory = mocked.constructed().get(0);
            verify(factory).createConsumer(keyDeserializer, valueDeserializer);
            verifyNoMoreInteractions(factory);
        }
    }
}