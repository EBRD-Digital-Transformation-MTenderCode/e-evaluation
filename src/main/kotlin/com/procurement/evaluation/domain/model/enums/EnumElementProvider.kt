package com.procurement.evaluation.domain.model.enums

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.exception.EnumElementProviderException

abstract class EnumElementProvider<T>(val info: EnumInfo<T>) where T : Enum<T>,
                                                                   T : EnumElementProvider.Key {
    interface Key {
        val key: String
    }

    class EnumInfo<T>(
        val target: Class<T>,
        val values: Array<T>
    )

    companion object {
        inline fun <reified T : Enum<T>> info() = EnumInfo(target = T::class.java, values = enumValues())
    }

    private val elements: Map<String, T> = info.values.associateBy { it.key.toUpperCase() }

    val allowedValues: List<String> = info.values.map { it.key }

    fun orNull(key: String): T? = elements[key.toUpperCase()]

    fun orThrow(key: String): T = orNull(key)
        ?: throw EnumElementProviderException(
            enumType = info.target.canonicalName,
            value = key,
            values = info.values.joinToString { it.key }
        )

    fun tryOf(key: String): Result<T, String> {
        val element = orNull(key)
        return if (element == null) {
            val enumType = info.target.canonicalName
            val allowedValues = info.values.joinToString { it.key }
            Result.failure("Unknown value '$key' for enum type '$enumType'. Allowed values are '$allowedValues'.")
        } else
            Result.success(element)
    }
}
