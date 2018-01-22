package com.procurement.evaluation.controller;

import com.procurement.evaluation.exception.ValidationException;
import com.procurement.evaluation.model.dto.errors.ValidationErrorResponse;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerExceptionHandler {
    private static final String MESSAGE = "Houston we have a problem";

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ValidationErrorResponse handleValidationContractProcessPeriod(
        final ValidationException e) {
        return new ValidationErrorResponse(
            MESSAGE,
            e.getErrors()
             .getFieldErrors()
             .stream()
             .map(f -> new ValidationErrorResponse.ErrorPoint(
                 f.getField(),
                 f.getDefaultMessage(),
                 f.getCode()))
             .collect(Collectors.toList()));
    }
}
