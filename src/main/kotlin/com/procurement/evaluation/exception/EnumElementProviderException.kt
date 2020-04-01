package com.procurement.evaluation.exception

data class EnumElementProviderException(private val enumType: String, val value: String, val values: String) :
    RuntimeException(
        "Unknown value for enumType $enumType: $value, Allowed values are $values"
    )
