package com.manus.kafka.core.validation;

/**
 * Exception thrown when a configuration property has an invalid value.
 */
public class InvalidPropertyValueException extends ConfigurationException {
    
    private final String propertyName;
    private final Object actualValue;
    private final Object expectedValue;
    
    /**
     * Creates a new InvalidPropertyValueException.
     *
     * @param propertyName the name of the property with invalid value
     * @param actualValue the actual invalid value
     * @param expectedValue the expected value or format
     */
    public InvalidPropertyValueException(String propertyName, Object actualValue, Object expectedValue) {
        super(ValidationError.invalidValue(propertyName, actualValue, expectedValue));
        this.propertyName = propertyName;
        this.actualValue = actualValue;
        this.expectedValue = expectedValue;
    }
    
    /**
     * Creates a new InvalidPropertyValueException with a custom message.
     *
     * @param propertyName the name of the property with invalid value
     * @param actualValue the actual invalid value
     * @param message the custom error message
     */
    public InvalidPropertyValueException(String propertyName, Object actualValue, String message) {
        super(new ValidationError(propertyName, message, ValidationErrorType.INVALID_VALUE));
        this.propertyName = propertyName;
        this.actualValue = actualValue;
        this.expectedValue = null;
    }
    
    /**
     * Gets the name of the property with invalid value.
     *
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }
    
    /**
     * Gets the actual invalid value.
     *
     * @return the actual value
     */
    public Object getActualValue() {
        return actualValue;
    }
    
    /**
     * Gets the expected value or format.
     *
     * @return the expected value, or null if not specified
     */
    public Object getExpectedValue() {
        return expectedValue;
    }
}