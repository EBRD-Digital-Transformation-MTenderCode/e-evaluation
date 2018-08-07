package com.procurement.evaluation.exception

enum class ErrorType constructor(val code: String, val message: String) {
    INVALID_JSON_TYPE("00.00", "Invalid type: "),
    DATA_NOT_FOUND("00.01", "Award not found."),
    INVALID_OWNER("00.02", "Invalid owner."),
    INVALID_STATUS("00.03", "Invalid status of award"),
    INVALID_STATUS_DETAILS("00.04", "Invalid status details of award"),
    INVALID_TOKEN("00.05", "Invalid token."),
    PERIOD_NOT_FOUND("01.01", "Period not found."),
    BIDS_RULES_NOT_FOUND("02.02", "Bids rules not found.");
}
