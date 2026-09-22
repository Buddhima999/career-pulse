package com.careerpulse.api.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NormalizedSizeValidator
        implements ConstraintValidator<NormalizedSize, String> {

    private int min;
    private int max;

    @Override
    public void initialize(NormalizedSize annotation) {
        this.min = annotation.min();
        this.max = annotation.max();
    }

    @Override
    public boolean isValid(
            String value,
            ConstraintValidatorContext context
    ) {
        if (value == null) {
            return true;
        }

        String normalizedValue = value
                .trim()
                .replaceAll("\\s+", " ");

        int length = normalizedValue.length();

        return length >= min && length <= max;
    }
}