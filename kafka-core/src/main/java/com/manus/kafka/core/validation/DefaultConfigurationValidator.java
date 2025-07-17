package com.manus.kafka.core.validation;

import java.util.Arrays;
import java.util.Map;

/**
 * Default implementation of ConfigurationValidator that provides comprehensive
 * validation for Kafka configuration properties with detailed error reporting.
 */
public class DefaultConfigurationValidator implements ConfigurationValidator {
    
    @Override
    public ValidationResult validate(Map<String, Object> properties) {
        ValidationResult result = new ValidationResult();
        
        if (properties == null) {
            result.addError(new ValidationError(null, "Configuration properties cannot be null", ValidationErrorType.GENERAL));
            return result;
        }
        
        // Perform basic validations
        result.merge(validateRequiredProperties(properties, "bootstrap.servers"));
        result.merge(checkForWarnings(properties));
        
        return result;
    }
    
    @Override
    public ValidationResult validateRequiredProperties(Map<String, Object> properties, String... requiredProperties) {
        ValidationResult result = new ValidationResult();
        
        if (properties == null) {
            result.addError(new ValidationError(null, "Configuration properties cannot be null", ValidationErrorType.GENERAL));
            return result;
        }
        
        for (String requiredProperty : requiredProperties) {
            if (!properties.containsKey(requiredProperty)) {
                result.addError(ValidationError.missingRequired(requiredProperty));
            } else {
                Object value = properties.get(requiredProperty);
                if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
                    result.addError(ValidationError.missingRequired(requiredProperty));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public ValidationResult validateAllowedValues(Map<String, Object> properties, String propertyKey, String... allowedValues) {
        ValidationResult result = new ValidationResult();
        
        if (properties == null || !properties.containsKey(propertyKey)) {
            return result; // Property not set, nothing to validate
        }
        
        Object value = properties.get(propertyKey);
        if (value == null) {
            return result; // Property is null, nothing to validate
        }
        
        String stringValue = value.toString();
        boolean valid = Arrays.asList(allowedValues).contains(stringValue);
        
        if (!valid) {
            result.addError(ValidationError.invalidValue(
                propertyKey, 
                stringValue, 
                "one of: " + String.join(", ", allowedValues)
            ));
        }
        
        return result;
    }
    
    @Override
    public ValidationResult validateRange(Map<String, Object> properties, String propertyKey, long min, long max) {
        ValidationResult result = new ValidationResult();
        
        if (properties == null || !properties.containsKey(propertyKey)) {
            return result; // Property not set, nothing to validate
        }
        
        Object value = properties.get(propertyKey);
        if (value == null) {
            return result; // Property is null, nothing to validate
        }
        
        long longValue;
        try {
            if (value instanceof Number) {
                longValue = ((Number) value).longValue();
            } else {
                longValue = Long.parseLong(value.toString());
            }
        } catch (NumberFormatException e) {
            result.addError(new ValidationError(
                propertyKey,
                "Invalid numeric value: " + value,
                ValidationErrorType.INVALID_FORMAT,
                "Value must be a valid number"
            ));
            return result;
        }
        
        if (longValue < min || longValue > max) {
            result.addError(ValidationError.outOfRange(propertyKey, longValue, min, max));
        }
        
        return result;
    }
    
    @Override
    public ValidationResult validateClass(Map<String, Object> properties, String propertyKey, Class<?> expectedType) {
        ValidationResult result = new ValidationResult();
        
        if (properties == null || !properties.containsKey(propertyKey)) {
            return result; // Property not set, nothing to validate
        }
        
        Object value = properties.get(propertyKey);
        if (value == null) {
            return result; // Property is null, nothing to validate
        }
        
        String className = value.toString();
        try {
            Class<?> clazz = Class.forName(className);
            if (!expectedType.isAssignableFrom(clazz)) {
                result.addError(new ValidationError(
                    propertyKey,
                    "Class " + className + " does not implement or extend " + expectedType.getName(),
                    ValidationErrorType.INVALID_CLASS,
                    "Ensure the class implements the required interface or extends the required class"
                ));
            }
        } catch (ClassNotFoundException e) {
            result.addError(new ValidationError(
                propertyKey,
                "Class not found: " + className,
                ValidationErrorType.INVALID_CLASS,
                "Ensure the class is available on the classpath"
            ));
        }
        
        return result;
    }
    
    @Override
    public ValidationResult validateCustom(Map<String, Object> properties, String propertyKey, PropertyValidator validator) {
        if (properties == null || !properties.containsKey(propertyKey) || validator == null) {
            return ValidationResult.success();
        }
        
        Object value = properties.get(propertyKey);
        return validator.validate(propertyKey, value);
    }
    
    @Override
    public ValidationResult checkForWarnings(Map<String, Object> properties) {
        ValidationResult result = new ValidationResult();
        
        if (properties == null) {
            return result;
        }
        
        // Check for single bootstrap server
        checkBootstrapServers(properties, result);
        
        // Check for potentially problematic producer settings
        result.merge(checkProducerWarnings(properties));
        
        // Check for potentially problematic consumer settings
        result.merge(checkConsumerWarnings(properties));
        
        return result;
    }
    
    @Override
    public ValidationResult validateProducerProperties(Map<String, Object> properties) {
        ValidationResult result = new ValidationResult();
        
        // Validate required producer properties
        result.merge(validateRequiredProperties(properties, "bootstrap.servers", "key.serializer", "value.serializer"));
        
        // Validate acks setting
        result.merge(validateAllowedValues(properties, "acks", "0", "1", "all", "-1"));
        
        // Validate compression type
        result.merge(validateAllowedValues(properties, "compression.type", "none", "gzip", "snappy", "lz4", "zstd"));
        
        // Validate numeric ranges
        result.merge(validateRange(properties, "retries", 0, Integer.MAX_VALUE));
        result.merge(validateRange(properties, "batch.size", 0, Integer.MAX_VALUE));
        result.merge(validateRange(properties, "linger.ms", 0, Long.MAX_VALUE));
        result.merge(validateRange(properties, "buffer.memory", 0, Long.MAX_VALUE));
        
        // Check for producer-specific warnings
        result.merge(checkProducerWarnings(properties));
        
        return result;
    }
    
    @Override
    public ValidationResult validateConsumerProperties(Map<String, Object> properties) {
        ValidationResult result = new ValidationResult();
        
        // Validate required consumer properties
        result.merge(validateRequiredProperties(properties, "bootstrap.servers", "group.id", "key.deserializer", "value.deserializer"));
        
        // Validate auto offset reset
        result.merge(validateAllowedValues(properties, "auto.offset.reset", "earliest", "latest", "none"));
        
        // Validate numeric ranges
        result.merge(validateRange(properties, "session.timeout.ms", 1, 3600000)); // 1ms to 1 hour
        result.merge(validateRange(properties, "max.poll.records", 1, Integer.MAX_VALUE));
        result.merge(validateRange(properties, "fetch.min.bytes", 1, Integer.MAX_VALUE));
        result.merge(validateRange(properties, "fetch.max.wait.ms", 0, Integer.MAX_VALUE));
        
        // Check for consumer-specific warnings
        result.merge(checkConsumerWarnings(properties));
        
        return result;
    }
    
    @Override
    public ValidationResult validateAdminClientProperties(Map<String, Object> properties) {
        ValidationResult result = new ValidationResult();
        
        // Validate required admin client properties
        result.merge(validateRequiredProperties(properties, "bootstrap.servers"));
        
        // Validate numeric ranges
        result.merge(validateRange(properties, "request.timeout.ms", 1000, 300000)); // 1 second to 5 minutes
        result.merge(validateRange(properties, "retries", 0, Integer.MAX_VALUE));
        
        return result;
    }
    
    private void checkBootstrapServers(Map<String, Object> properties, ValidationResult result) {
        if (properties.containsKey("bootstrap.servers")) {
            String bootstrapServers = properties.get("bootstrap.servers").toString();
            if (!bootstrapServers.contains(",")) {
                result.addWarning(ValidationWarning.performance(
                    "bootstrap.servers",
                    "Only one bootstrap server is configured",
                    "Configure multiple bootstrap servers for better reliability: 'host1:9092,host2:9092'"
                ));
            }
        }
    }
    
    private ValidationResult checkProducerWarnings(Map<String, Object> properties) {
        ValidationResult result = new ValidationResult();
        
        // Check for acks=0
        if ("0".equals(properties.get("acks"))) {
            result.addWarning(ValidationWarning.reliability(
                "acks",
                "acks=0 provides no guarantee that records have been received by the broker",
                "Consider using acks=1 or acks=all for better durability guarantees"
            ));
        }
        
        // Check for retries=0
        if (Integer.valueOf(0).equals(properties.get("retries"))) {
            result.addWarning(ValidationWarning.reliability(
                "retries",
                "retries=0 means no retries will be performed",
                "Consider setting retries to a positive value to handle transient errors"
            ));
        }
        
        return result;
    }
    
    private ValidationResult checkConsumerWarnings(Map<String, Object> properties) {
        ValidationResult result = new ValidationResult();
        
        // Check for enable.auto.commit=true
        if ("true".equals(properties.get("enable.auto.commit"))) {
            result.addWarning(ValidationWarning.reliability(
                "enable.auto.commit",
                "enable.auto.commit=true may result in duplicate processing or message loss",
                "Consider using manual commit (enable.auto.commit=false) for better control"
            ));
        }
        
        // Check for auto.offset.reset=latest
        if ("latest".equals(properties.get("auto.offset.reset"))) {
            result.addWarning(ValidationWarning.performance(
                "auto.offset.reset",
                "auto.offset.reset=latest will cause the consumer to miss messages sent while offline",
                "Consider using 'earliest' if you need to process all messages"
            ));
        }
        
        return result;
    }
}