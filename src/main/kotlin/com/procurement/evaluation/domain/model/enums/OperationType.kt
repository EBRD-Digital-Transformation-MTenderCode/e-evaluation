package com.procurement.evaluation.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OperationType(@JsonValue override val key: String) : EnumElementProvider.Key {
    APPLY_QUALIFICATION_PROTOCOL("applyQualificationProtocol"),
    CANCEL_TENDER_EV("cancelTenderEv"),
    TENDER_PERIOD_END_AUCTION("tenderPeriodEndAuction"),
    TENDER_PERIOD_END_EV("tenderPeriodEndEv"),
    TENDER_UNSUCCESSFUL("tenderUnsuccessful");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = OperationType.orThrow(name)
    }
}