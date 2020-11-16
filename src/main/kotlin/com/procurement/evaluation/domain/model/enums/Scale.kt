package com.procurement.evaluation.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Scale(@JsonValue override val key: String) : EnumElementProvider.Key {
    MICRO("micro"),
    SME("sme"),
    LARGE("large"),
    EMPTY("");

    override fun toString(): String = key

    companion object : EnumElementProvider<Scale>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Scale.orThrow(name)
    }
}
