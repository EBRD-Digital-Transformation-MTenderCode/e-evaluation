package com.procurement.evaluation.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OperationType2(@JsonValue override val key: String) : EnumElementProvider.Key {

    APPLY_QUALIFICATION_PROTOCOL("applyQualificationProtocol"),
    CREATE_SUBMISSION("createSubmission"),
    DECLARE_NON_CONFLICT_OF_INTEREST("declareNonConflictOfInterest"),
    LOT_CANCELLATION("lotCancellation"),
    SUBMISSION_PERIOD_END("submissionPeriodEnd"),
    TENDER_CANCELLATION("tenderCancellation"),
    TENDER_OR_LOT_AMENDMENT_CANCELLATION("tenderOrLotAmendmentCancellation"),
    TENDER_OR_LOT_AMENDMENT_CONFIRMATION("tenderOrLotAmendmentConfirmation");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType2>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = orThrow(name)
    }
}
