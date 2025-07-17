package com.manus.kafka.core.validation;

import java.util.List;

/**
 * Exception thrown when required configuration properties are missing.
 */
public class MissingRequiredPropertyException extends ConfigurationException {
    
    private final List<String> missingProperties;
    
    /**
     * Creates a new MissingRequiredPropertyException.
     *
     * @param missingProperties the list of missing property names
     */
    public MissingRequiredPropertyException(List<String> missingProperties) {
        super(createValidationResult(missingProperties));
        this.missingProperties = missingProperties;
    }
    
    /**
     * Creates a new MissingRequiredPropertyException for a single property.
     *
     * @param propertyName the name of the missing property
     */
    public MissingRequiredPropertyException(String propertyName) {
        this(List.of(propertyName));
    }
    
    /**
     * Gets the list of missing property names.
     *
     * @return the missing property names
     */
    public List<String> getMissingProperties() {
        return missingProperties;
    }
    
    private static ValidationResult createValidationResult(List<String> missingProperties) {
        ValidationResult result = new ValidationResult();
        for (String property : missingProperties) {
            result.addError(ValidationError.missingRequired(property));
        }
        return result;
    }
}