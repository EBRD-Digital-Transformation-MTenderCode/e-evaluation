package com.procurement.evaluation.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.exception.EnumException

enum class Scale(@JsonValue val value: String) {
    MICRO("micro"),
    SME("sme"),
    LARGE("large"),
    EMPTY("");

    override fun toString(): String = value

    companion object {
        private val elements: Map<String, Scale> = values().associateBy { it.value.toUpperCase() }

        @JsonCreator
        @JvmStatic
        fun fromString(value: String): Scale = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = Scale::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}