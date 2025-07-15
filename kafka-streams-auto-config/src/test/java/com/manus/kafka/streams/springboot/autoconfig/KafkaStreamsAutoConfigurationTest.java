package com.manus.kafka.streams.springboot.autoconfig;

import com.manus.kafka.streams.config.KafkaStreamsProperties;
import com.manus.kafka.streams.factory.KafkaStreamsFactory;
import org.apache.kafka.streams.StreamsBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.config.KafkaStreamsConfiguration;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link KafkaStreamsAutoConfiguration}.
 */
public class KafkaStreamsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KafkaStreamsAutoConfiguration.class))
            .withPropertyValues(
                    "spring.kafka.bootstrap-servers=localhost:9092",
                    "spring.kafka.streams.application-id=test-streams-app"
            );

    @Test
    public void testDefaultConfiguration() {
        contextRunner.run(context -> {
            // Verify that all expected beans are created
            assertThat(context).hasSingleBean(KafkaStreamsProperties.class);
            assertThat(context).hasSingleBean(KafkaStreamsFactory.class);
            assertThat(context).hasSingleBean(StreamsBuilder.class);
            assertThat(context).hasSingleBean(KafkaStreamsConfiguration.class);
            assertThat(context).hasSingleBean(StreamsBuilderFactoryBean.class);
            
            // Verify that the properties are correctly configured
            KafkaStreamsProperties properties = context.getBean(KafkaStreamsProperties.class);
            assertThat(properties.asMap()).containsEntry("bootstrap.servers", "localhost:9092");
            assertThat(properties.asMap()).containsEntry("application.id", "test-streams-app");
        });
    }
    
    @Test
    public void testCustomConfiguration() {
        contextRunner
                .withPropertyValues(
                        "spring.kafka.streams.state-dir=/tmp/kafka-streams",
                        "spring.kafka.streams.num-stream-threads=2",
                        "spring.kafka.streams.replication-factor=1",
                        "spring.kafka.streams.commit-interval-ms=30000",
                        "spring.kafka.streams.processing-guarantee=exactly_once_v2",
                        "spring.kafka.streams.properties.max.task.idle.ms=500"
                )
                .run(context -> {
                    // Verify that the properties are correctly configured
                    KafkaStreamsProperties properties = context.getBean(KafkaStreamsProperties.class);
                    assertThat(properties.asMap()).containsEntry("state.dir", "/tmp/kafka-streams");
                    assertThat(properties.asMap()).containsEntry("num.stream.threads", 2);
                    assertThat(properties.asMap()).containsEntry("replication.factor", 1);
                    assertThat(properties.asMap()).containsEntry("commit.interval.ms", 30000L);
                    assertThat(properties.asMap()).containsEntry("processing.guarantee", "exactly_once_v2");
                    assertThat(properties.asMap()).containsEntry("max.task.idle.ms", "500");
                });
    }
    
    @Test
    public void testDisabledConfiguration() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(KafkaStreamsAutoConfiguration.class))
                .withPropertyValues("spring.kafka.streams.enabled=false")
                .run(context -> {
                    // Verify that no beans are created when disabled
                    assertThat(context).doesNotHaveBean(KafkaStreamsProperties.class);
                    assertThat(context).doesNotHaveBean(KafkaStreamsFactory.class);
                    assertThat(context).doesNotHaveBean(StreamsBuilder.class);
                    assertThat(context).doesNotHaveBean(KafkaStreamsConfiguration.class);
                    assertThat(context).doesNotHaveBean(StreamsBuilderFactoryBean.class);
                });
    }
}