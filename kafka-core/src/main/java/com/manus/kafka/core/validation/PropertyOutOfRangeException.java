package com.manus.kafka.core.validation;

/**
 * Exception thrown when a numeric configuration property value is out of the allowed range.
 */
public class PropertyOutOfRangeException extends ConfigurationException {
    
    private final String propertyName;
    private final Object actualValue;
    private final Object minValue;
    private final Object maxValue;
    
    /**
     * Creates a new PropertyOutOfRangeException.
     *
     * @param propertyName the name of the property with out-of-range value
     * @param actualValue the actual out-of-range value
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value
     */
    public PropertyOutOfRangeException(String propertyName, Object actualValue, Object minValue, Object maxValue) {
        super(ValidationError.outOfRange(propertyName, actualValue, minValue, maxValue));
        this.propertyName = propertyName;
        this.actualValue = actualValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
    
    /**
     * Gets the name of the property with out-of-range value.
     *
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }
    
    /**
     * Gets the actual out-of-range value.
     *
     * @return the actual value
     */
    public Object getActualValue() {
        return actualValue;
    }
    
    /**
     * Gets the minimum allowed value.
     *
     * @return the minimum value
     */
    public Object getMinValue() {
        return minValue;
    }
    
    /**
     * Gets the maximum allowed value.
     *
     * @return the maximum value
     */
    public Object getMaxValue() {
        return maxValue;
    }
}