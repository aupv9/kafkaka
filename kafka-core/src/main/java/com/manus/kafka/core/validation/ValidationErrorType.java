package com.manus.kafka.core.validation;

/**
 * Enumeration of different types of validation errors.
 */
public enum ValidationErrorType {
    
    /**
     * A required property is missing.
     */
    MISSING_REQUIRED,
    
    /**
     * A property has an invalid value.
     */
    INVALID_VALUE,
    
    /**
     * A property value is out of the allowed range.
     */
    OUT_OF_RANGE,
    
    /**
     * A property value is not one of the allowed values.
     */
    INVALID_CHOICE,
    
    /**
     * A class name is invalid or cannot be loaded.
     */
    INVALID_CLASS,
    
    /**
     * A property has an invalid format.
     */
    INVALID_FORMAT,
    
    /**
     * A general validation error.
     */
    GENERAL
}