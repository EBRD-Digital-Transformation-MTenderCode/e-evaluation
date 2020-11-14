package com.procurement.evaluation.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonValue

class ApiVersion2 private constructor(@JsonValue val underlying: String) : Comparable<ApiVersion2> {

    companion object {
        const val pattern: String = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\$"
        private val regex = pattern.toRegex()

        val NaN = ApiVersion2(0, 0, 0)

        fun orNull(version: String): ApiVersion2? = if (version.matches(regex)) ApiVersion2(version) else null

        fun orThrow(version: String, builder: (String) -> Exception): ApiVersion2 =
            if (version.matches(regex)) ApiVersion2(version) else throw builder(version)
    }

    constructor(major: Int, minor: Int, patch: Int) : this("$major.$minor.$patch")

    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is ApiVersion2
                && this.underlying == other.underlying
        else
            true
    }

    override fun hashCode(): Int = underlying.hashCode()

    override fun toString(): String = underlying

    override fun compareTo(other: ApiVersion2): Int = underlying.compareTo(other.underlying)
}