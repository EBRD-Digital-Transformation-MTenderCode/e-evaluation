package com.procurement.evaluation.domain.model

import com.fasterxml.jackson.annotation.JsonValue


class Cpid private constructor(private val value_: String) {
    val value: String get() = value_

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Cpid
                && this.value_ == other.value_
        else
            true
    }

    override fun hashCode(): Int = value_.hashCode()

    @JsonValue
    override fun toString(): String = value_

    companion object {
        private val regex = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}\$".toRegex()

        val pattern: String
            get() = regex.pattern

        fun tryCreateOrNull(value: String): Cpid? = if (value.matches(regex)) Cpid(value_ = value) else null
    }
}
