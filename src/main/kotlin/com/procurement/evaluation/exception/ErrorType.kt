package com.procurement.evaluation.exception

enum class ErrorType constructor(val code: String, val message: String) {
    JSON_TYPE("00.00", "Invalid type: "),
    DATA_NOT_FOUND("00.01", "Award not found."),
    OWNER("00.02", "Invalid owner."),
    STATUS("00.03", "Invalid status of award"),
    STATUS_DETAILS("00.04", "Invalid status details of award"),
    ID("00.05", "Invalid id of award"),
    TOKEN("00.06", "Invalid token."),
    PERIOD_NOT_FOUND("01.01", "Period not found."),
    PERIOD_INVALID("01.02", "Invalid period."),
    BIDS_RULES("02.02", "Bids rules not found."),
    RELATED_LOTS("02.04", "Related lots in docs not found in award related lots(VR 7.4.4)"),
    DOC_TYPE("02.05", "Invalid document type Award by bid(VR 7.4.9)"),
    ALREADY_HAVE_ACTIVE_AWARDS("02.06", "Active status already have(BR-7.4.15(1.a)"),
    STATUS_DETAILS_SAVED_AWARD("02.07", "Saved award have incorrect status details."),
    CONTEXT("20.01", "Context parameter not found.");
}
