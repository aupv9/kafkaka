# Kafka Streams Auto-Configuration

This module provides auto-configuration for Kafka Streams applications in Spring Boot.

## Features

- Automatic configuration of Kafka Streams properties
- Integration with Spring Kafka's Streams support
- Factory for creating Kafka Streams instances
- Support for custom serializers and deserializers
- Conditional auto-configuration based on classpath and properties

## Usage

### Maven Dependency

Add the following dependency to your project:

```xml
<dependency>
    <groupId>com.manus.kafka</groupId>
    <artifactId>kafka-streams-auto-config</artifactId>
    <version>${version}</version>
</dependency>
```

### Configuration Properties

Configure Kafka Streams in your `application.properties` or `application.yml`:

```properties
# Required properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.streams.application-id=my-streams-app

# Optional properties
spring.kafka.streams.state-dir=/tmp/kafka-streams
spring.kafka.streams.num-stream-threads=2
spring.kafka.streams.replication-factor=1
spring.kafka.streams.default-key-serde=org.apache.kafka.common.serialization.Serdes$StringSerde
spring.kafka.streams.default-value-serde=org.apache.kafka.common.serialization.Serdes$StringSerde
spring.kafka.streams.commit-interval-ms=30000
spring.kafka.streams.processing-guarantee=exactly_once_v2

# Additional properties
spring.kafka.streams.properties.max.task.idle.ms=500
```

### Creating a Kafka Streams Topology

Create a `StreamsBuilder` bean to define your topology:

```java
@Configuration
public class KafkaStreamsConfig {

    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        KStream<String, String> stream = streamsBuilder.stream("input-topic");
        stream.mapValues(value -> value.toUpperCase())
              .to("output-topic");
        return stream;
    }
}
```

### Using the KafkaStreamsFactory

You can also use the `KafkaStreamsFactory` directly:

```java
@Service
public class MyStreamsService {

    private final KafkaStreamsFactory streamsFactory;
    
    public MyStreamsService(KafkaStreamsFactory streamsFactory) {
        this.streamsFactory = streamsFactory;
    }
    
    public void createAndStartStreams() {
        StreamsBuilder builder = new StreamsBuilder();
        // Define your topology
        KStream<String, String> stream = builder.stream("input-topic");
        stream.mapValues(value -> value.toUpperCase())
              .to("output-topic");
        
        // Create and start the streams
        KafkaStreams streams = streamsFactory.createKafkaStreams(builder);
        streams.start();
    }
}
```

## Customization

You can customize the auto-configuration by providing your own beans:

```java
@Configuration
public class CustomKafkaStreamsConfig {

    @Bean
    public KafkaStreamsProperties kafkaStreamsProperties() {
        KafkaStreamsProperties properties = new KafkaStreamsProperties();
        properties.applicationId("custom-app-id");
        properties.bootstrapServers("localhost:9092");
        properties.numStreamThreads(4);
        // Add more properties
        return properties;
    }
    
    @Bean
    public KafkaStreamsFactory kafkaStreamsFactory(KafkaStreamsProperties properties) {
        return new KafkaStreamsFactory(properties);
    }
}
```

## Security

To configure security for Kafka Streams:

```properties
spring.kafka.security.protocol=SASL_SSL
spring.kafka.security.sasl-mechanism=PLAIN
spring.kafka.security.sasl-jaas-config=org.apache.kafka.common.security.plain.PlainLoginModule required username="user" password="password";
```