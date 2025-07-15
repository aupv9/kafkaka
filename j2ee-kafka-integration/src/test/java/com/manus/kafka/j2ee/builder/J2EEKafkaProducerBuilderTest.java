package com.manus.kafka.j2ee.builder;

import com.manus.kafka.core.config.KafkaProducerProperties;
import com.manus.kafka.core.factory.KafkaProducerFactory;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedConstruction;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link J2EEKafkaProducerBuilder}.
 */
public class J2EEKafkaProducerBuilderTest {

    private Producer<String, String> mockProducer;

    @BeforeEach
    public void setUp() {
        mockProducer = mock(Producer.class);
    }

    @Test
    @DisplayName("Test default constructor creates default properties")
    public void testDefaultConstructor() {
        // When
        J2EEKafkaProducerBuilder<String, String> builder = new J2EEKafkaProducerBuilder<>();

        // Then
        KafkaProducerProperties properties = builder.getProperties();
        assertNotNull(properties);
        assertEquals("all", properties.asMap().get("acks"));
        assertEquals(3, properties.asMap().get("retries"));
    }

    @Test
    @DisplayName("Test constructor with properties")
    public void testConstructorWithProperties() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        properties.bootstrapServers("localhost:9092");

        // When
        J2EEKafkaProducerBuilder<String, String> builder = new J2EEKafkaProducerBuilder<>(properties);

        // Then
        assertSame(properties, builder.getProperties());
        assertEquals("localhost:9092", properties.asMap().get("bootstrap.servers"));
    }

    @Test
    @DisplayName("Test builder methods set properties correctly")
    public void testBuilderMethods() {
        // When
        J2EEKafkaProducerBuilder<String, String> builder = new J2EEKafkaProducerBuilder<String, String>()
            .bootstrapServers("localhost:9092")
            .clientId("test-client")
            .acks("1")
            .retries(5)
            .batchSize(32768)
            .lingerMs(10L)
            .bufferMemory(67108864L)
            .keySerializer("org.apache.kafka.common.serialization.StringSerializer")
            .valueSerializer("org.apache.kafka.common.serialization.StringSerializer")
            .compressionType("gzip")
            .securityProtocol("SASL_SSL")
            .saslMechanism("PLAIN")
            .saslJaasConfig("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");

        // Then
        KafkaProducerProperties properties = builder.getProperties();
        Map<String, Object> map = properties.asMap();
        assertEquals("localhost:9092", map.get("bootstrap.servers"));
        assertEquals("test-client", map.get("client.id"));
        assertEquals("1", map.get("acks"));
        assertEquals(5, map.get("retries"));
        assertEquals(32768, map.get("batch.size"));
        assertEquals(10L, map.get("linger.ms"));
        assertEquals(67108864L, map.get("buffer.memory"));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", map.get("key.serializer"));
        assertEquals("org.apache.kafka.common.serialization.StringSerializer", map.get("value.serializer"));
        assertEquals("gzip", map.get("compression.type"));
        assertEquals("SASL_SSL", map.get("security.protocol"));
        assertEquals("PLAIN", map.get("sasl.mechanism"));
        assertEquals("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";", 
                     map.get("sasl.jaas.config"));
    }

    @Test
    @DisplayName("Test property method sets custom property")
    public void testPropertyMethod() {
        // When
        J2EEKafkaProducerBuilder<String, String> builder = new J2EEKafkaProducerBuilder<String, String>()
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
        J2EEKafkaProducerBuilder<String, String> builder = new J2EEKafkaProducerBuilder<String, String>()
            .properties(customProps);

        // Then
        assertEquals("value1", builder.getProperties().asMap().get("prop1"));
        assertEquals("value2", builder.getProperties().asMap().get("prop2"));
    }

    @Test
    @DisplayName("Test build method creates producer with string serializers")
    public void testBuildWithStringSerializers() {
        try (MockedConstruction<KafkaProducerFactory> mocked = mockConstruction(
                KafkaProducerFactory.class,
                (mock, context) -> {
                    when(mock.createProducer()).thenReturn(mockProducer);
                })) {

            // Given
            J2EEKafkaProducerBuilder<String, String> builder = new J2EEKafkaProducerBuilder<String, String>()
                .bootstrapServers("localhost:9092")
                .keySerializer("org.apache.kafka.common.serialization.StringSerializer")
                .valueSerializer("org.apache.kafka.common.serialization.StringSerializer");

            // When
            Producer<String, String> producer = builder.build();

            // Then
            assertSame(mockProducer, producer);
            assertEquals(1, mocked.constructed().size());
            KafkaProducerFactory factory = mocked.constructed().get(0);
            verify(factory).createProducer();
            verifyNoMoreInteractions(factory);
        }
    }

    @Test
    @DisplayName("Test build method creates producer with serializer instances")
    public void testBuildWithSerializerInstances() {
        try (MockedConstruction<KafkaProducerFactory> mocked = mockConstruction(
                KafkaProducerFactory.class,
                (mock, context) -> {
                    when(mock.createProducer(any(StringSerializer.class), any(StringSerializer.class)))
                        .thenReturn(mockProducer);
                })) {

            // Given
            StringSerializer keySerializer = new StringSerializer();
            StringSerializer valueSerializer = new StringSerializer();

            J2EEKafkaProducerBuilder<String, String> builder = new J2EEKafkaProducerBuilder<String, String>()
                .bootstrapServers("localhost:9092")
                .keySerializer(keySerializer)
                .valueSerializer(valueSerializer);

            // When
            Producer<String, String> producer = builder.build();

            // Then
            assertSame(mockProducer, producer);
            assertEquals(1, mocked.constructed().size());
            KafkaProducerFactory factory = mocked.constructed().get(0);
            verify(factory).createProducer(keySerializer, valueSerializer);
            verifyNoMoreInteractions(factory);
        }
    }
}
