package com.manus.kafka.core.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the result of a configuration validation operation.
 * Contains validation errors and warnings that occurred during validation.
 */
public class ValidationResult {
    
    private final List<ValidationError> errors;
    private final List<ValidationWarning> warnings;
    
    /**
     * Creates a new ValidationResult with empty errors and warnings.
     */
    public ValidationResult() {
        this.errors = new ArrayList<>();
        this.warnings = new ArrayList<>();
    }
    
    /**
     * Creates a new ValidationResult with the specified errors and warnings.
     *
     * @param errors the validation errors
     * @param warnings the validation warnings
     */
    public ValidationResult(List<ValidationError> errors, List<ValidationWarning> warnings) {
        this.errors = new ArrayList<>(errors != null ? errors : Collections.emptyList());
        this.warnings = new ArrayList<>(warnings != null ? warnings : Collections.emptyList());
    }
    
    /**
     * Adds a validation error to this result.
     *
     * @param error the validation error to add
     */
    public void addError(ValidationError error) {
        if (error != null) {
            this.errors.add(error);
        }
    }
    
    /**
     * Adds a validation warning to this result.
     *
     * @param warning the validation warning to add
     */
    public void addWarning(ValidationWarning warning) {
        if (warning != null) {
            this.warnings.add(warning);
        }
    }
    
    /**
     * Returns true if this result contains any validation errors.
     *
     * @return true if there are validation errors, false otherwise
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    /**
     * Returns true if this result contains any validation warnings.
     *
     * @return true if there are validation warnings, false otherwise
     */
    public boolean hasWarnings() {
        return !warnings.isEmpty();
    }
    
    /**
     * Returns true if the validation was successful (no errors).
     *
     * @return true if validation was successful, false otherwise
     */
    public boolean isValid() {
        return !hasErrors();
    }
    
    /**
     * Gets the list of validation errors.
     *
     * @return an unmodifiable list of validation errors
     */
    public List<ValidationError> getErrors() {
        return Collections.unmodifiableList(errors);
    }
    
    /**
     * Gets the list of validation warnings.
     *
     * @return an unmodifiable list of validation warnings
     */
    public List<ValidationWarning> getWarnings() {
        return Collections.unmodifiableList(warnings);
    }
    
    /**
     * Gets the total number of validation issues (errors + warnings).
     *
     * @return the total number of validation issues
     */
    public int getTotalIssues() {
        return errors.size() + warnings.size();
    }
    
    /**
     * Merges another ValidationResult into this one.
     *
     * @param other the other ValidationResult to merge
     */
    public void merge(ValidationResult other) {
        if (other != null) {
            this.errors.addAll(other.errors);
            this.warnings.addAll(other.warnings);
        }
    }
    
    /**
     * Creates a new ValidationResult that represents a successful validation.
     *
     * @return a ValidationResult with no errors or warnings
     */
    public static ValidationResult success() {
        return new ValidationResult();
    }
    
    /**
     * Creates a new ValidationResult with a single error.
     *
     * @param error the validation error
     * @return a ValidationResult containing the error
     */
    public static ValidationResult error(ValidationError error) {
        ValidationResult result = new ValidationResult();
        result.addError(error);
        return result;
    }
    
    /**
     * Creates a new ValidationResult with a single warning.
     *
     * @param warning the validation warning
     * @return a ValidationResult containing the warning
     */
    public static ValidationResult warning(ValidationWarning warning) {
        ValidationResult result = new ValidationResult();
        result.addWarning(warning);
        return result;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ValidationResult{");
        sb.append("errors=").append(errors.size());
        sb.append(", warnings=").append(warnings.size());
        sb.append(", valid=").append(isValid());
        sb.append('}');
        return sb.toString();
    }
}