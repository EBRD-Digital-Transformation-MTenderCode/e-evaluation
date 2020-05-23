package com.procurement.evaluation.domain.model.person

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.Result.Companion.failure
import com.procurement.evaluation.domain.functional.Result.Companion.success
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

class PersonId private constructor(private val value: String) {

    @JsonValue
    override fun toString(): String = value

    companion object {

        @JvmStatic
        @JsonCreator
        fun parse(text: String): PersonId? = if (text.isBlank())
            null
        else
            PersonId(text)

        fun tryCreate(text: String): Result<PersonId, DataErrors> =
            if (text.isBlank())
                failure(DataErrors.Validation.EmptyString(name = "id"))
            else
                success(PersonId(text))
    }
}
