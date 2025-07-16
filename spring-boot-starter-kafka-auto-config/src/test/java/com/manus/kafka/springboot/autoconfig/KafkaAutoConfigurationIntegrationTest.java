package com.manus.kafka.springboot.autoconfig;

import com.manus.kafka.core.config.KafkaAdminClientProperties;
import com.manus.kafka.core.config.KafkaConsumerProperties;
import com.manus.kafka.core.config.KafkaProducerProperties;
import com.manus.kafka.core.factory.KafkaAdminClientFactory;
import com.manus.kafka.core.factory.KafkaConsumerFactory;
import com.manus.kafka.core.factory.KafkaProducerFactory;
import com.manus.kafka.springboot.properties.KafkaProperties;
import org.apache.kafka.clients.admin.AdminClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link KafkaAutoConfiguration}.
 */
public class KafkaAutoConfigurationIntegrationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration.class));

    @Test
    @DisplayName("Test auto-configuration with minimal properties")
    public void testAutoConfigurationWithMinimalProperties() {
        this.contextRunner
                .withPropertyValues("spring.kafka.bootstrap-servers=localhost:9092")
                .run(context -> {
                    // Verify that all expected beans are created
                    assertThat(context).hasSingleBean(KafkaProperties.class);
                    assertThat(context).hasSingleBean(KafkaProducerProperties.class);
                    assertThat(context).hasSingleBean(KafkaConsumerProperties.class);
                    assertThat(context).hasSingleBean(KafkaAdminClientProperties.class);
                    assertThat(context).hasSingleBean(KafkaProducerFactory.class);
                    assertThat(context).hasSingleBean(KafkaConsumerFactory.class);
                    assertThat(context).hasSingleBean(KafkaAdminClientFactory.class);
                    assertThat(context).hasSingleBean(DefaultKafkaProducerFactory.class);
                    assertThat(context).hasSingleBean(DefaultKafkaConsumerFactory.class);
                    assertThat(context).hasSingleBean(KafkaAdmin.class);
                    assertThat(context).hasSingleBean(KafkaTemplate.class);
                    assertThat(context).hasSingleBean(AdminClient.class);

                    // Verify properties are correctly configured
                    KafkaProperties kafkaProperties = context.getBean(KafkaProperties.class);
                    assertThat(kafkaProperties.getBootstrapServers()).isEqualTo("localhost:9092");
                });
    }

    @Test
    @DisplayName("Test auto-configuration with comprehensive properties")
    public void testAutoConfigurationWithComprehensiveProperties() {
        this.contextRunner
                .withPropertyValues(
                        "spring.kafka.bootstrap-servers=localhost:9092,localhost:9093",
                        "spring.kafka.client-id=test-client",
                        "spring.kafka.producer.acks=all",
                        "spring.kafka.producer.retries=3",
                        "spring.kafka.producer.compression-type=gzip",
                        "spring.kafka.consumer.group-id=test-group",
                        "spring.kafka.consumer.enable-auto-commit=false",
                        "spring.kafka.consumer.auto-offset-reset=earliest",
                        "spring.kafka.admin.request-timeout-ms=60000",
                        "spring.kafka.security.protocol=SASL_SSL",
                        "spring.kafka.security.sasl-mechanism=PLAIN"
                )
                .run(context -> {
                    // Verify properties are correctly configured
                    KafkaProducerProperties producerProps = context.getBean(KafkaProducerProperties.class);
                    Map<String, Object> producerMap = producerProps.asMap();
                    assertThat(producerMap.get("bootstrap.servers")).isEqualTo("localhost:9092,localhost:9093");
                    assertThat(producerMap.get("client.id")).isEqualTo("test-client");
                    assertThat(producerMap.get("acks")).isEqualTo("all");
                    assertThat(producerMap.get("retries")).isEqualTo(3);
                    assertThat(producerMap.get("compression.type")).isEqualTo("gzip");
                    assertThat(producerMap.get("security.protocol")).isEqualTo("SASL_SSL");
                    assertThat(producerMap.get("sasl.mechanism")).isEqualTo("PLAIN");

                    KafkaConsumerProperties consumerProps = context.getBean(KafkaConsumerProperties.class);
                    Map<String, Object> consumerMap = consumerProps.asMap();
                    assertThat(consumerMap.get("bootstrap.servers")).isEqualTo("localhost:9092,localhost:9093");
                    assertThat(consumerMap.get("client.id")).isEqualTo("test-client");
                    assertThat(consumerMap.get("group.id")).isEqualTo("test-group");
                    assertThat(consumerMap.get("enable.auto.commit")).isEqualTo("false");
                    assertThat(consumerMap.get("auto.offset.reset")).isEqualTo("earliest");
                    assertThat(consumerMap.get("security.protocol")).isEqualTo("SASL_SSL");
                    assertThat(consumerMap.get("sasl.mechanism")).isEqualTo("PLAIN");

                    KafkaAdminClientProperties adminProps = context.getBean(KafkaAdminClientProperties.class);
                    Map<String, Object> adminMap = adminProps.asMap();
                    assertThat(adminMap.get("bootstrap.servers")).isEqualTo("localhost:9092,localhost:9093");
                    assertThat(adminMap.get("client.id")).isEqualTo("test-client");
                    assertThat(adminMap.get("request.timeout.ms")).isEqualTo(60000);
                    assertThat(adminMap.get("security.protocol")).isEqualTo("SASL_SSL");
                    assertThat(adminMap.get("sasl.mechanism")).isEqualTo("PLAIN");
                });
    }

    @Test
    @DisplayName("Test auto-configuration with custom properties")
    public void testAutoConfigurationWithCustomProperties() {
        this.contextRunner
                .withPropertyValues(
                        "spring.kafka.bootstrap-servers=localhost:9092",
                        "spring.kafka.properties.custom.property=custom-value",
                        "spring.kafka.producer.properties.producer.custom=producer-value",
                        "spring.kafka.consumer.properties.consumer.custom=consumer-value",
                        "spring.kafka.admin.properties.admin.custom=admin-value"
                )
                .run(context -> {
                    KafkaProducerProperties producerProps = context.getBean(KafkaProducerProperties.class);
                    Map<String, Object> producerMap = producerProps.asMap();
                    assertThat(producerMap.get("custom.property")).isEqualTo("custom-value");
                    assertThat(producerMap.get("producer.custom")).isEqualTo("producer-value");

                    KafkaConsumerProperties consumerProps = context.getBean(KafkaConsumerProperties.class);
                    Map<String, Object> consumerMap = consumerProps.asMap();
                    assertThat(consumerMap.get("custom.property")).isEqualTo("custom-value");
                    assertThat(consumerMap.get("consumer.custom")).isEqualTo("consumer-value");

                    KafkaAdminClientProperties adminProps = context.getBean(KafkaAdminClientProperties.class);
                    Map<String, Object> adminMap = adminProps.asMap();
                    assertThat(adminMap.get("custom.property")).isEqualTo("custom-value");
                    assertThat(adminMap.get("admin.custom")).isEqualTo("admin-value");
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {"PLAINTEXT", "SSL", "SASL_PLAINTEXT", "SASL_SSL"})
    @DisplayName("Test auto-configuration with various security protocols")
    public void testAutoConfigurationWithSecurityProtocols(String securityProtocol) {
        this.contextRunner
                .withPropertyValues(
                        "spring.kafka.bootstrap-servers=localhost:9092",
                        "spring.kafka.security.protocol=" + securityProtocol
                )
                .run(context -> {
                    KafkaProducerProperties producerProps = context.getBean(KafkaProducerProperties.class);
                    assertThat(producerProps.asMap().get("security.protocol")).isEqualTo(securityProtocol);

                    KafkaConsumerProperties consumerProps = context.getBean(KafkaConsumerProperties.class);
                    assertThat(consumerProps.asMap().get("security.protocol")).isEqualTo(securityProtocol);

                    KafkaAdminClientProperties adminProps = context.getBean(KafkaAdminClientProperties.class);
                    assertThat(adminProps.asMap().get("security.protocol")).isEqualTo(securityProtocol);
                });
    }

    @Test
    @DisplayName("Test auto-configuration with SSL properties")
    public void testAutoConfigurationWithSslProperties() {
        this.contextRunner
                .withPropertyValues(
                        "spring.kafka.bootstrap-servers=localhost:9092",
                        "spring.kafka.security.protocol=SSL",
                        "spring.kafka.ssl.truststore-location=/path/to/truststore.jks",
                        "spring.kafka.ssl.truststore-password=truststore-password",
                        "spring.kafka.ssl.keystore-location=/path/to/keystore.jks",
                        "spring.kafka.ssl.keystore-password=keystore-password",
                        "spring.kafka.ssl.key-password=key-password"
                )
                .run(context -> {
                    KafkaProducerProperties producerProps = context.getBean(KafkaProducerProperties.class);
                    Map<String, Object> producerMap = producerProps.asMap();
                    assertThat(producerMap.get("security.protocol")).isEqualTo("SSL");
                    assertThat(producerMap.get("ssl.truststore.location")).isEqualTo("/path/to/truststore.jks");
                    assertThat(producerMap.get("ssl.truststore.password")).isEqualTo("truststore-password");
                    assertThat(producerMap.get("ssl.keystore.location")).isEqualTo("/path/to/keystore.jks");
                    assertThat(producerMap.get("ssl.keystore.password")).isEqualTo("keystore-password");
                    assertThat(producerMap.get("ssl.key.password")).isEqualTo("key-password");
                });
    }

    @Test
    @DisplayName("Test auto-configuration with SASL properties")
    public void testAutoConfigurationWithSaslProperties() {
        this.contextRunner
                .withPropertyValues(
                        "spring.kafka.bootstrap-servers=localhost:9092",
                        "spring.kafka.security.protocol=SASL_PLAINTEXT",
                        "spring.kafka.security.sasl-mechanism=PLAIN",
                        "spring.kafka.security.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";"
                )
                .run(context -> {
                    KafkaProducerProperties producerProps = context.getBean(KafkaProducerProperties.class);
                    Map<String, Object> producerMap = producerProps.asMap();
                    assertThat(producerMap.get("security.protocol")).isEqualTo("SASL_PLAINTEXT");
                    assertThat(producerMap.get("sasl.mechanism")).isEqualTo("PLAIN");
                    assertThat(producerMap.get("sasl.jaas.config")).isEqualTo("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"user\" password=\"password\";");
                });
    }

    @Test
    @DisplayName("Test auto-configuration does not create beans when disabled")
    public void testAutoConfigurationDisabled() {
        this.contextRunner
                .withPropertyValues("spring.kafka.enabled=false")
                .run(context -> {
                    // When auto-configuration is disabled, no Kafka beans should be created
                    assertThat(context).doesNotHaveBean(KafkaProperties.class);
                    assertThat(context).doesNotHaveBean(KafkaProducerProperties.class);
                    assertThat(context).doesNotHaveBean(KafkaConsumerProperties.class);
                    assertThat(context).doesNotHaveBean(KafkaAdminClientProperties.class);
                });
    }

    @Test
    @DisplayName("Test auto-configuration with custom bean overrides")
    public void testAutoConfigurationWithCustomBeanOverrides() {
        this.contextRunner
                .withPropertyValues("spring.kafka.bootstrap-servers=localhost:9092")
                .withUserConfiguration(CustomKafkaConfiguration.class)
                .run(context -> {
                    // Verify that custom beans take precedence
                    assertThat(context).hasSingleBean(KafkaProducerFactory.class);
                    assertThat(context).hasSingleBean(KafkaConsumerFactory.class);
                    
                    // Verify that the custom beans are used
                    KafkaProducerFactory<?, ?> producerFactory = context.getBean(KafkaProducerFactory.class);
                    assertThat(producerFactory).isInstanceOf(CustomKafkaProducerFactory.class);
                    
                    KafkaConsumerFactory<?, ?> consumerFactory = context.getBean(KafkaConsumerFactory.class);
                    assertThat(consumerFactory).isInstanceOf(CustomKafkaConsumerFactory.class);
                });
    }

    @Test
    @DisplayName("Test auto-configuration with missing bootstrap servers")
    public void testAutoConfigurationWithMissingBootstrapServers() {
        this.contextRunner
                .run(context -> {
                    // Without bootstrap servers, the auto-configuration should still work
                    // but the properties should have default values
                    assertThat(context).hasSingleBean(KafkaProperties.class);
                    
                    KafkaProperties kafkaProperties = context.getBean(KafkaProperties.class);
                    // Default bootstrap servers should be used
                    assertThat(kafkaProperties.getBootstrapServers()).isNotNull();
                });
    }

    @Test
    @DisplayName("Test auto-configuration with producer-only configuration")
    public void testAutoConfigurationWithProducerOnlyConfiguration() {
        this.contextRunner
                .withPropertyValues(
                        "spring.kafka.bootstrap-servers=localhost:9092",
                        "spring.kafka.producer.acks=all",
                        "spring.kafka.producer.retries=5",
                        "spring.kafka.producer.batch-size=32768"
                )
                .run(context -> {
                    KafkaProducerProperties producerProps = context.getBean(KafkaProducerProperties.class);
                    Map<String, Object> producerMap = producerProps.asMap();
                    assertThat(producerMap.get("acks")).isEqualTo("all");
                    assertThat(producerMap.get("retries")).isEqualTo(5);
                    assertThat(producerMap.get("batch.size")).isEqualTo(32768);

                    // Consumer should still be configured with defaults
                    assertThat(context).hasSingleBean(KafkaConsumerProperties.class);
                });
    }

    @Test
    @DisplayName("Test auto-configuration with consumer-only configuration")
    public void testAutoConfigurationWithConsumerOnlyConfiguration() {
        this.contextRunner
                .withPropertyValues(
                        "spring.kafka.bootstrap-servers=localhost:9092",
                        "spring.kafka.consumer.group-id=test-consumer-group",
                        "spring.kafka.consumer.auto-offset-reset=latest",
                        "spring.kafka.consumer.max-poll-records=1000"
                )
                .run(context -> {
                    KafkaConsumerProperties consumerProps = context.getBean(KafkaConsumerProperties.class);
                    Map<String, Object> consumerMap = consumerProps.asMap();
                    assertThat(consumerMap.get("group.id")).isEqualTo("test-consumer-group");
                    assertThat(consumerMap.get("auto.offset.reset")).isEqualTo("latest");
                    assertThat(consumerMap.get("max.poll.records")).isEqualTo(1000);

                    // Producer should still be configured with defaults
                    assertThat(context).hasSingleBean(KafkaProducerProperties.class);
                });
    }

    @Test
    @DisplayName("Test auto-configuration with admin-only configuration")
    public void testAutoConfigurationWithAdminOnlyConfiguration() {
        this.contextRunner
                .withPropertyValues(
                        "spring.kafka.bootstrap-servers=localhost:9092",
                        "spring.kafka.admin.request-timeout-ms=45000",
                        "spring.kafka.admin.retries=10"
                )
                .run(context -> {
                    KafkaAdminClientProperties adminProps = context.getBean(KafkaAdminClientProperties.class);
                    Map<String, Object> adminMap = adminProps.asMap();
                    assertThat(adminMap.get("request.timeout.ms")).isEqualTo(45000);
                    assertThat(adminMap.get("retries")).isEqualTo(10);

                    // Producer and consumer should still be configured
                    assertThat(context).hasSingleBean(KafkaProducerProperties.class);
                    assertThat(context).hasSingleBean(KafkaConsumerProperties.class);
                });
    }

    @Configuration
    static class CustomKafkaConfiguration {

        @Bean
        public KafkaProducerFactory<?, ?> customKafkaProducerFactory() {
            return new CustomKafkaProducerFactory<>();
        }

        @Bean
        public KafkaConsumerFactory<?, ?> customKafkaConsumerFactory() {
            return new CustomKafkaConsumerFactory<>();
        }
    }

    static class CustomKafkaProducerFactory<K, V> extends KafkaProducerFactory<K, V> {
        // Custom implementation for testing
    }

    static class CustomKafkaConsumerFactory<K, V> extends KafkaConsumerFactory<K, V> {
        // Custom implementation for testing
    }
}