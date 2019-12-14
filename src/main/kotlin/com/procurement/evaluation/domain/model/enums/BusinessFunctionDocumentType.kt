package com.procurement.evaluation.domain.model.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.exception.EnumException

enum class BusinessFunctionDocumentType(@JsonValue val value: String) {
    REGULATORY_DOCUMENT("regulatoryDocument");

    override fun toString(): String {
        return this.value
    }

    companion object {
        private val elements: Map<String, BusinessFunctionDocumentType> =
            values().associateBy { it.value.toUpperCase() }

        @JsonCreator
        @JvmStatic
        fun fromString(value: String): BusinessFunctionDocumentType = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = BusinessFunctionDocumentType::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }
}
