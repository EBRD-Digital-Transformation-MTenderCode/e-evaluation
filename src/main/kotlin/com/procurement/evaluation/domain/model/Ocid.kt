package com.procurement.evaluation.domain.model

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.enums.EnumElementProvider.Companion.keysAsStrings
import com.procurement.evaluation.domain.model.enums.Stage
import java.io.Serializable

class Ocid private constructor(private val value: String, val stage: Stage) : Serializable {
    override fun equals(other: Any?): Boolean {
        return if (this !== other)
            other is Ocid
                && this.value == other.value
        else
            true
    }

    override fun hashCode(): Int = value.hashCode()

    @JsonValue
    override fun toString(): String = value

    companion object {
        private const val STAGE_POSITION = 4
        private val STAGES: String
            get() = Stage.allowedElements.keysAsStrings()
                .joinToString(separator = "|", prefix = "(", postfix = ")") { it.toUpperCase() }

        private val regex = "^[a-z]{4}-[a-z0-9]{6}-[A-Z]{2}-[0-9]{13}-$STAGES-[0-9]{13}\$".toRegex()

        val pattern: String
            get() = regex.pattern


        @JvmStatic
        @JsonCreator
        fun tryCreateOrNull(value: String): Ocid? =
            if (value.matches(regex)) {
                val stage = Stage.orNull(value.split("-")[STAGE_POSITION])!!
                Ocid(value = value, stage = stage)
            } else
                null

        fun tryCreate(value: String): Result<Ocid, String> =
            if (value.matches(regex)) {
                val stage = Stage.orNull(value.split("-")[STAGE_POSITION])!!
                Result.success(Ocid(value = value, stage = stage))
            } else
                Result.failure(pattern)
    }
}