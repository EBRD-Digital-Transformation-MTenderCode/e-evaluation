package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.model.enums.EnumElementProvider

enum class DataType(@JsonValue override val key: String) : EnumElementProvider.Key {

    BOOLEAN("boolean"),
    INTEGER("integer"),
    NUMBER("number"),
    STRING("string");

    override fun toString(): String = key

    companion object : EnumElementProvider<DataType>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = DataType.orThrow(name)
    }
}