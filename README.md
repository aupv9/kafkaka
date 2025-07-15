# Kafka Auto Configuration Library

A comprehensive library for automatic configuration of Kafka clients in both Spring Boot and J2EE environments.

## Overview

This library provides a modular approach to Kafka client configuration, with sensible defaults and easy customization. It consists of three main modules:

1. **Core Module**: Framework-agnostic Kafka client configuration
2. **Spring Boot Starter**: Auto-configuration for Spring Boot applications
3. **J2EE Integration**: Integration for traditional J2EE applications

## Modules

### Core Module

The core module provides the foundation for Kafka client configuration, independent of any specific framework. It includes:

- Configuration classes for Kafka producers, consumers, and admin clients
- Factory classes for creating Kafka clients
- Utility classes for loading and validating properties

#### Usage Example

```java
// Create producer properties with defaults
KafkaProducerProperties producerProps = new KafkaProducerProperties()
    .bootstrapServers("localhost:9092")
    .keySerializer(StringSerializer.class.getName())
    .valueSerializer(StringSerializer.class.getName());

// Create a producer factory
KafkaProducerFactory<String, String> factory = new KafkaProducerFactory<>(producerProps);

// Create a producer
Producer<String, String> producer = factory.createProducer();
```

### Spring Boot Starter

The Spring Boot starter provides automatic configuration for Kafka in Spring Boot applications. It includes:

- Auto-configuration for Kafka producers, consumers, and admin clients
- Configuration properties mapping from Spring Boot properties
- Integration with Spring Kafka

#### Usage Example

1. Add the dependency to your project:

```xml
<dependency>
    <groupId>com.manus.kafka</groupId>
    <artifactId>spring-boot-starter-kafka-auto-config</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

2. Configure Kafka in your `application.properties` or `application.yml`:

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.consumer.group-id=my-group
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
```

3. Inject and use the auto-configured beans:

```java
@Service
public class MyService {
    
    private final KafkaTemplate<String, String> kafkaTemplate;
    
    public MyService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    
    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
```

### J2EE Integration

The J2EE integration module provides integration for traditional J2EE applications. It includes:

- Builder classes for creating Kafka clients
- Configuration source abstractions for loading properties from different sources
- Lifecycle management utilities

#### Usage Example

```java
// Create a producer using the builder
Producer<String, String> producer = new J2EEKafkaProducerBuilder<String, String>()
    .bootstrapServers("localhost:9092")
    .keySerializer(StringSerializer.class.getName())
    .valueSerializer(StringSerializer.class.getName())
    .build();

// Register the producer with the lifecycle manager
KafkaLifecycleManager.getInstance().registerProducer(producer);

// Load configuration from environment variables
ConfigurationSource configSource = new EnvironmentConfigurationSource("KAFKA_");
Map<String, Object> config = configSource.getProperties();

// Create a consumer using the builder
Consumer<String, String> consumer = new J2EEKafkaConsumerBuilder<String, String>()
    .bootstrapServers("localhost:9092")
    .groupId("my-group")
    .keyDeserializer(StringDeserializer.class.getName())
    .valueDeserializer(StringDeserializer.class.getName())
    .build();

// Register the consumer with the lifecycle manager
KafkaLifecycleManager.getInstance().registerConsumer(consumer);
```

## Configuration Sources

The J2EE integration module provides several configuration sources:

- **FileConfigurationSource**: Loads properties from a file
- **JndiConfigurationSource**: Loads properties from JNDI
- **EnvironmentConfigurationSource**: Loads properties from environment variables

## Lifecycle Management

The J2EE integration module provides a lifecycle manager for Kafka clients:

- **KafkaLifecycleManager**: Manages the lifecycle of Kafka clients in a J2EE environment

## Best Practices

The library enforces several best practices:

- **Producer**: 
  - `acks=all` by default for durability
  - Reasonable defaults for batching and retries

- **Consumer**: 
  - `enable.auto.commit=false` by default for better control
  - `auto.offset.reset=earliest` by default to avoid missing messages

- **Security**:
  - Support for SASL and SSL
  - Easy configuration of security properties

## Requirements

- Java 17 or higher
- Apache Kafka 3.6.1 or higher
- Spring Boot 3.2.0 or higher (for Spring Boot starter)
- Jakarta EE 9 or higher (for J2EE integration)