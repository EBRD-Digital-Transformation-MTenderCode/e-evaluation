package com.procurement.evaluation.infrastructure.dto

import com.procurement.evaluation.domain.functional.Result

data class ApiVersion2(val major: Int, val minor: Int, val patch: Int) {
    companion object {
        fun tryValueOf(version: String): Result<ApiVersion2, String> {
            val elements = version.split(".")
            if (elements.isEmpty() || elements.size != 3)
                return Result.failure("Invalid value of the api version ($version).")

            val major: Int = elements[0].toIntOrNull()
                ?: return Result.failure("Invalid value of the api version ($version).")

            val minor: Int = elements[1].toIntOrNull()
                ?: return Result.failure("Invalid value of the api version ($version).")

            val patch: Int = elements[2].toIntOrNull()
                ?: return Result.failure("Invalid value of the api version ($version).")

            return Result.success(ApiVersion2(major = major, minor = minor, patch = patch))
        }
    }

    override fun toString(): String = "$major.$minor.$patch"
}
