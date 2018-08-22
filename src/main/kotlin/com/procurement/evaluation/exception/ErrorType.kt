package com.procurement.evaluation.exception

enum class ErrorType constructor(val code: String, val message: String) {
    INVALID_JSON_TYPE("00.00", "Invalid type: "),
    DATA_NOT_FOUND("00.01", "Award not found."),
    INVALID_OWNER("00.02", "Invalid owner."),
    INVALID_STATUS("00.03", "Invalid status of award"),
    INVALID_STATUS_DETAILS("00.04", "Invalid status details of award"),
    INVALID_ID("00.05", "Invalid id of award"),
    INVALID_TOKEN("00.06", "Invalid token."),
    PERIOD_NOT_FOUND("01.01", "Period not found."),
    BIDS_RULES_NOT_FOUND("02.02", "Bids rules not found."),
    DATE_IS_NOT_IN_PERIOD("02.03","Period invalid"),
    RELATED_LOTS_IN_DOCS_ARE_INVALID("02.04","Related lots in docs not found in award related lots(VR 7.4.4)"),
    INVALID_DOC_TYPE("02.05","Invalid document type Award by bid(VR 7.4.9)"),
    ALREADY_HAVE_ACTIVE_AWARDS("02.06","Active status already have(BR-7.4.15(1.a)"),
    INVALID_STATUS_DETAILS_SAVED_AWARD("02.07","Saved award have incorrect status details BR-7.4.15");
}
