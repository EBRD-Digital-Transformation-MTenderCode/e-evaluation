package com.procurement.evaluation.exception;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ValidationException extends RuntimeException {
    private final BindingResult errors;

    public ValidationException(final BindingResult errors) {
        this.errors = errors;
    }
}
