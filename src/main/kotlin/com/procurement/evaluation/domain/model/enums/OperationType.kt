package com.procurement.evaluation.domain.model.enums

import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.exception.EnumException

enum class OperationType(@JsonValue val value: String) {
    TENDER_UNSUCCESSFUL("tenderUnsuccessful"),
    TENDER_PERIOD_END_EV("tenderPeriodEndEv"),
    TENDER_PERIOD_END_AUCTION("tenderPeriodEndAuction"),
    CANCEL_TENDER_EV("cancelTenderEv");

    override fun toString(): String {
        return this.value
    }
    companion object {
        private val elements: Map<String, OperationType> = values().associateBy { it.value.toUpperCase() }
        fun fromString(value: String): OperationType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = OperationType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}