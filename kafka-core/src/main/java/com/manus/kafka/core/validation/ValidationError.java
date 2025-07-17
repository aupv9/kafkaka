package com.manus.kafka.core.validation;

import java.util.Objects;

/**
 * Represents a validation error that occurred during configuration validation.
 * Contains detailed information about the error including the property name,
 * error message, and optional recovery suggestions.
 */
public class ValidationError {
    
    private final String propertyName;
    private final String message;
    private final String suggestion;
    private final ValidationErrorType type;
    private final Object actualValue;
    private final Object expectedValue;
    
    /**
     * Creates a new ValidationError with the specified details.
     *
     * @param propertyName the name of the property that failed validation
     * @param message the error message
     * @param type the type of validation error
     */
    public ValidationError(String propertyName, String message, ValidationErrorType type) {
        this(propertyName, message, type, null, null, null);
    }
    
    /**
     * Creates a new ValidationError with the specified details and suggestion.
     *
     * @param propertyName the name of the property that failed validation
     * @param message the error message
     * @param type the type of validation error
     * @param suggestion optional suggestion for fixing the error
     */
    public ValidationError(String propertyName, String message, ValidationErrorType type, String suggestion) {
        this(propertyName, message, type, suggestion, null, null);
    }
    
    /**
     * Creates a new ValidationError with complete details.
     *
     * @param propertyName the name of the property that failed validation
     * @param message the error message
     * @param type the type of validation error
     * @param suggestion optional suggestion for fixing the error
     * @param actualValue the actual value that failed validation
     * @param expectedValue the expected value or format
     */
    public ValidationError(String propertyName, String message, ValidationErrorType type, 
                          String suggestion, Object actualValue, Object expectedValue) {
        this.propertyName = propertyName;
        this.message = message;
        this.type = type != null ? type : ValidationErrorType.GENERAL;
        this.suggestion = suggestion;
        this.actualValue = actualValue;
        this.expectedValue = expectedValue;
    }
    
    /**
     * Gets the name of the property that failed validation.
     *
     * @return the property name
     */
    public String getPropertyName() {
        return propertyName;
    }
    
    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the suggestion for fixing the error.
     *
     * @return the suggestion, or null if none provided
     */
    public String getSuggestion() {
        return suggestion;
    }
    
    /**
     * Gets the type of validation error.
     *
     * @return the validation error type
     */
    public ValidationErrorType getType() {
        return type;
    }
    
    /**
     * Gets the actual value that failed validation.
     *
     * @return the actual value, or null if not provided
     */
    public Object getActualValue() {
        return actualValue;
    }
    
    /**
     * Gets the expected value or format.
     *
     * @return the expected value, or null if not provided
     */
    public Object getExpectedValue() {
        return expectedValue;
    }
    
    /**
     * Returns true if this error has a suggestion for fixing it.
     *
     * @return true if suggestion is available, false otherwise
     */
    public boolean hasSuggestion() {
        return suggestion != null && !suggestion.trim().isEmpty();
    }
    
    /**
     * Gets a formatted error message including suggestion if available.
     *
     * @return the formatted error message
     */
    public String getFormattedMessage() {
        StringBuilder sb = new StringBuilder();
        if (propertyName != null) {
            sb.append("[").append(propertyName).append("] ");
        }
        sb.append(message);
        if (hasSuggestion()) {
            sb.append(" Suggestion: ").append(suggestion);
        }
        return sb.toString();
    }
    
    /**
     * Creates a ValidationError for a missing required property.
     *
     * @param propertyName the name of the missing property
     * @return a ValidationError for the missing property
     */
    public static ValidationError missingRequired(String propertyName) {
        return new ValidationError(
            propertyName,
            "Required property is missing",
            ValidationErrorType.MISSING_REQUIRED,
            "Please provide a value for this required property"
        );
    }
    
    /**
     * Creates a ValidationError for an invalid property value.
     *
     * @param propertyName the name of the property
     * @param actualValue the actual invalid value
     * @param expectedValue the expected value or format
     * @return a ValidationError for the invalid value
     */
    public static ValidationError invalidValue(String propertyName, Object actualValue, Object expectedValue) {
        return new ValidationError(
            propertyName,
            "Invalid value: " + actualValue,
            ValidationErrorType.INVALID_VALUE,
            "Expected: " + expectedValue,
            actualValue,
            expectedValue
        );
    }
    
    /**
     * Creates a ValidationError for a value that is out of range.
     *
     * @param propertyName the name of the property
     * @param actualValue the actual value
     * @param minValue the minimum allowed value
     * @param maxValue the maximum allowed value
     * @return a ValidationError for the out-of-range value
     */
    public static ValidationError outOfRange(String propertyName, Object actualValue, Object minValue, Object maxValue) {
        return new ValidationError(
            propertyName,
            "Value " + actualValue + " is out of range",
            ValidationErrorType.OUT_OF_RANGE,
            "Value must be between " + minValue + " and " + maxValue,
            actualValue,
            minValue + " - " + maxValue
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValidationError that = (ValidationError) o;
        return Objects.equals(propertyName, that.propertyName) &&
               Objects.equals(message, that.message) &&
               type == that.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(propertyName, message, type);
    }
    
    @Override
    public String toString() {
        return getFormattedMessage();
    }
}