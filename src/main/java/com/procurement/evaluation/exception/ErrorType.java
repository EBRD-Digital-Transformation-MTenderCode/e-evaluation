package com.procurement.evaluation.exception;

public enum ErrorType {

    DATA_NOT_FOUND("00.01", "Award not found."),
    INVALID_OWNER("00.02", "Invalid owner."),
    INVALID_STATUS_DETAILS("00.03", "Invalid Award status details."),
    PERIOD_NOT_FOUND("01.01", "Period not found."),
    BIDS_RULES_NOT_FOUND("02.02", "Bids rules not found.");

    private final String code;
    private final String message;

    ErrorType(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}
