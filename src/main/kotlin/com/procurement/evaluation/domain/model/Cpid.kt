package com.procurement.evaluation.domain.model

import com.fasterxml.jackson.annotation.JsonValue


class Cpid private constructor(val underlying: String) {

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Cpid
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    @JsonValue
    override fun toString(): String = underlying

    companion object {
        private val regex = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}\$".toRegex()

        val pattern: String
            get() = regex.pattern

        fun tryCreateOrNull(value: String): Cpid? = if (value.matches(regex)) Cpid(underlying = value) else null
    }
}
