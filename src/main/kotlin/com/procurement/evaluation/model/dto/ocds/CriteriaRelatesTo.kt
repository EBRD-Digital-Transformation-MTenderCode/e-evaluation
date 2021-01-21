package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.model.enums.EnumElementProvider

enum class CriteriaRelatesTo(@JsonValue override val key: String) : EnumElementProvider.Key {
    TENDERER("tenderer"),
    ITEM("item"),
    LOT("lot"),
    TENDER("tender");

    override fun toString(): String = key

    companion object : EnumElementProvider<CriteriaRelatesTo>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = CriteriaRelatesTo.orThrow(name)
    }
}