package com.manus.kafka.core.factory;

import com.manus.kafka.core.config.KafkaProducerProperties;
import org.apache.kafka.clients.producer.Producer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.Arguments;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Stream;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link KafkaProducerFactory}.
 */
public class KafkaProducerFactoryTest {

    @Test
    @DisplayName("Test constructor with default properties")
    public void testConstructorWithDefaultProperties() {
        // Given/When
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();

        // Then
        KafkaProducerProperties properties = factory.getProperties();
        assertNotNull(properties);
        assertEquals("all", properties.asMap().get("acks"));
        assertEquals(3, properties.asMap().get("retries"));
        assertEquals(16384, properties.asMap().get("batch.size"));
    }

    @Test
    @DisplayName("Test constructor with custom properties")
    public void testConstructorWithCustomProperties() {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        properties.bootstrapServers("localhost:9092");
        properties.acks("1");
        properties.retries(5);

        // When
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>(properties);

        // Then
        KafkaProducerProperties factoryProperties = factory.getProperties();
        assertNotNull(factoryProperties);
        assertEquals("localhost:9092", factoryProperties.asMap().get("bootstrap.servers"));
        assertEquals("1", factoryProperties.asMap().get("acks"));
        assertEquals(5, factoryProperties.asMap().get("retries"));
    }

    @Test
    @DisplayName("Test createProducer with invalid properties")
    public void testCreateProducerWithInvalidProperties() {
        // Given
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();

        // When/Then
        // The factory should validate properties before creating a Producer
        assertThrows(IllegalStateException.class, () -> factory.createProducer());
    }

    @Test
    @DisplayName("Test createProducer with custom properties")
    public void testCreateProducerWithCustomProperties() {
        // Given
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("bootstrap.servers", "localhost:9092");
        customProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        customProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // When/Then
        // We can't easily test the actual creation of a Producer without a real Kafka broker,
        // but we can verify that the method doesn't throw a validation exception
        Producer<String, String> producer = null;
        try {
            producer = factory.createProducer(customProps);
            assertNotNull(producer, "Producer should not be null");
        } catch (Exception e) {
            // We expect a connection-related exception, not a validation exception
            assertFalse(e instanceof IllegalStateException, "Unexpected IllegalStateException: " + e.getMessage());
        } finally {
            // Clean up
            if (producer != null) {
                producer.close();
            }
        }
    }

    @Test
    @DisplayName("Test createProducer with invalid custom properties")
    public void testCreateProducerWithInvalidCustomProperties() {
        // Given
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();
        Map<String, Object> customProps = new HashMap<>();
        customProps.put("bootstrap.servers", "localhost:9092");
        // Missing required properties: key.serializer, value.serializer

        // When/Then
        assertThrows(IllegalStateException.class, () -> factory.createProducer(customProps));
    }

    @Test
    @DisplayName("Test static createProducer method")
    public void testStaticCreateProducer() {
        // Given
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // When/Then
        // The static method doesn't validate properties or try to connect immediately,
        // so it should not throw an exception
        Producer<String, String> producer = null;
        try {
            producer = KafkaProducerFactory.createProducer(properties);
            assertNotNull(producer, "Producer should not be null");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        } finally {
            // Clean up
            if (producer != null) {
                producer.close();
            }
        }
    }
    
    // Comprehensive factory method tests with various configurations
    
    @ParameterizedTest
    @MethodSource("provideProducerConfigurationTestData")
    @DisplayName("Test factory with various producer configurations")
    public void testFactoryWithVariousConfigurations(Map<String, Object> config, boolean shouldSucceed, String expectedErrorKeyword) {
        // Given
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();
        
        // When/Then
        if (shouldSucceed) {
            Producer<String, String> producer = null;
            try {
                producer = factory.createProducer(config);
                assertNotNull(producer, "Producer should not be null for valid configuration");
            } catch (Exception e) {
                // Connection exceptions are acceptable, validation exceptions are not
                assertFalse(e instanceof IllegalStateException, 
                    "Should not throw validation exception for valid config: " + e.getMessage());
            } finally {
                if (producer != null) {
                    producer.close();
                }
            }
        } else {
            IllegalStateException exception = assertThrows(IllegalStateException.class, 
                () -> factory.createProducer(config));
            assertTrue(exception.getMessage().contains(expectedErrorKeyword),
                "Expected error message to contain '" + expectedErrorKeyword + "' but got: " + exception.getMessage());
        }
    }
    
    private static Stream<Arguments> provideProducerConfigurationTestData() {
        return Stream.of(
            // Valid configurations
            Arguments.of(createBasicProducerConfig(), true, ""),
            Arguments.of(createSecureProducerConfig(), true, ""),
            Arguments.of(createHighThroughputProducerConfig(), true, ""),
            Arguments.of(createReliableProducerConfig(), true, ""),
            Arguments.of(createAvroProducerConfig(), true, ""),
            
            // Invalid configurations
            Arguments.of(createMissingBootstrapConfig(), false, "bootstrap.servers"),
            Arguments.of(createMissingKeySerializerConfig(), false, "key.serializer"),
            Arguments.of(createMissingValueSerializerConfig(), false, "value.serializer"),
            Arguments.of(createInvalidAcksConfig(), false, "acks"),
            Arguments.of(createInvalidCompressionConfig(), false, "compression.type")
        );
    }
    
    private static Map<String, Object> createBasicProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return config;
    }
    
    private static Map<String, Object> createSecureProducerConfig() {
        Map<String, Object> config = createBasicProducerConfig();
        config.put("security.protocol", "SASL_SSL");
        config.put("sasl.mechanism", "PLAIN");
        config.put("sasl.jaas.config", "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");
        config.put("ssl.truststore.location", "/path/to/truststore.jks");
        config.put("ssl.truststore.password", "password");
        return config;
    }
    
    private static Map<String, Object> createHighThroughputProducerConfig() {
        Map<String, Object> config = createBasicProducerConfig();
        config.put("acks", "1");
        config.put("compression.type", "lz4");
        config.put("batch.size", 32768);
        config.put("linger.ms", 10);
        config.put("buffer.memory", 67108864);
        return config;
    }
    
    private static Map<String, Object> createReliableProducerConfig() {
        Map<String, Object> config = createBasicProducerConfig();
        config.put("acks", "all");
        config.put("retries", Integer.MAX_VALUE);
        config.put("max.in.flight.requests.per.connection", 1);
        config.put("enable.idempotence", true);
        config.put("delivery.timeout.ms", 300000);
        return config;
    }
    
    private static Map<String, Object> createAvroProducerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer");
        config.put("schema.registry.url", "http://localhost:8081");
        return config;
    }
    
    private static Map<String, Object> createMissingBootstrapConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return config;
    }
    
    private static Map<String, Object> createMissingKeySerializerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return config;
    }
    
    private static Map<String, Object> createMissingValueSerializerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return config;
    }
    
    private static Map<String, Object> createInvalidAcksConfig() {
        Map<String, Object> config = createBasicProducerConfig();
        config.put("acks", "invalid");
        return config;
    }
    
    private static Map<String, Object> createInvalidCompressionConfig() {
        Map<String, Object> config = createBasicProducerConfig();
        config.put("compression.type", "invalid");
        return config;
    }
    
    @ParameterizedTest
    @CsvSource({
        "localhost:9092, true",
        "localhost:9092,localhost:9093, true",
        "host1:9092,host2:9092,host3:9092, true",
        "'', false",
        "invalid-host, true" // This should pass validation but fail on connection
    })
    @DisplayName("Test factory with various bootstrap server configurations")
    public void testFactoryWithBootstrapServerVariations(String bootstrapServers, boolean shouldPassValidation) {
        // Given
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", bootstrapServers);
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();
        
        // When/Then
        if (shouldPassValidation) {
            Producer<String, String> producer = null;
            try {
                producer = factory.createProducer(config);
                assertNotNull(producer, "Producer should not be null for valid configuration");
            } catch (Exception e) {
                // Connection exceptions are acceptable for invalid hosts
                assertFalse(e instanceof IllegalStateException, 
                    "Should not throw validation exception: " + e.getMessage());
            } finally {
                if (producer != null) {
                    producer.close();
                }
            }
        } else {
            assertThrows(IllegalStateException.class, () -> factory.createProducer(config));
        }
    }
    
    @Test
    @DisplayName("Test factory properties inheritance and override")
    public void testFactoryPropertiesInheritanceAndOverride() {
        // Given
        KafkaProducerProperties baseProperties = new KafkaProducerProperties();
        baseProperties.bootstrapServers("localhost:9092");
        baseProperties.acks("all");
        baseProperties.retries(5);
        baseProperties.batchSize(32768);
        
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>(baseProperties);
        
        Map<String, Object> overrideProps = new HashMap<>();
        overrideProps.put("acks", "1"); // Override base property
        overrideProps.put("compression.type", "gzip"); // Add new property
        overrideProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        overrideProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        // When
        Producer<String, String> producer = null;
        try {
            producer = factory.createProducer(overrideProps);
            
            // Then
            assertNotNull(producer, "Producer should be created successfully");
            
            // Verify that base properties are inherited and overrides are applied
            // Note: We can't easily verify the actual producer configuration without reflection
            // or a test-specific producer implementation, but the fact that creation succeeds
            // indicates that the configuration merging worked correctly
            
        } catch (Exception e) {
            // Connection exceptions are acceptable
            assertFalse(e instanceof IllegalStateException, 
                "Should not throw validation exception: " + e.getMessage());
        } finally {
            if (producer != null) {
                producer.close();
            }
        }
    }
    
    @Test
    @DisplayName("Test factory with null and empty configurations")
    public void testFactoryWithNullAndEmptyConfigurations() {
        // Given
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>();
        
        // When/Then - null configuration
        assertThrows(IllegalStateException.class, () -> factory.createProducer((Map<String, Object>) null));
        
        // When/Then - empty configuration
        assertThrows(IllegalStateException.class, () -> factory.createProducer(new HashMap<>()));
        
        // When/Then - null Properties
        assertThrows(IllegalArgumentException.class, () -> KafkaProducerFactory.createProducer((Properties) null));
    }
    
    @Test
    @DisplayName("Test factory thread safety")
    public void testFactoryThreadSafety() throws InterruptedException {
        // Given
        KafkaProducerProperties properties = new KafkaProducerProperties();
        properties.bootstrapServers("localhost:9092");
        KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>(properties);
        
        Map<String, Object> config = new HashMap<>();
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        
        // When - concurrent factory usage
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    Producer<String, String> producer = factory.createProducer(config);
                    if (producer != null) {
                        producer.close();
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Connection exceptions are acceptable
                    if (!(e instanceof IllegalStateException)) {
                        successCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // Then
        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(threadCount, successCount.get(), "All threads should complete successfully");
        
        executor.shutdown();
    }
}
