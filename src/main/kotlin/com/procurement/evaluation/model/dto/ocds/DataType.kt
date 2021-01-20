package com.procurement.evaluation.model.dto.ocds

import com.procurement.evaluation.domain.model.enums.EnumElementProvider

enum class DataType(override val key: String) : EnumElementProvider.Key {

    BOOLEAN("boolean"),
    INTEGER("integer"),
    NUMBER("number"),
    STRING("string");

    override fun toString(): kotlin.String = key

    companion object : EnumElementProvider<DataType>(info = info())
}