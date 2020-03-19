package com.procurement.evaluation.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class OperationType(@JsonValue override val key: String) : EnumElementProvider.Key {
    TENDER_UNSUCCESSFUL("tenderUnsuccessful"),
    TENDER_PERIOD_END_EV("tenderPeriodEndEv"),
    TENDER_PERIOD_END_AUCTION("tenderPeriodEndAuction"),
    CANCEL_TENDER_EV("cancelTenderEv");

    override fun toString(): String = key

    companion object : EnumElementProvider<OperationType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = OperationType.orThrow(name)
    }
}