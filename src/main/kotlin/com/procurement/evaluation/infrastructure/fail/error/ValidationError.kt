package com.procurement.evaluation.infrastructure.fail.error

import com.procurement.evaluation.infrastructure.fail.Fail

sealed class ValidationError(numberError: String, override val description: String) : Fail.Error("VR-") {
    override val code: String = prefix + numberError

    class InvalidToken() : ValidationError(numberError = "10.2.4.1", description = "Request token doesn't match token from the database.")

    class InvalidOwner() : ValidationError(numberError = "10.2.4.2", description = "Request owner doesn't match owner from the database.")

    class AwardNotFound() : ValidationError(numberError = "10.4.2.3", description = "Award not found.")

    class TendererNotLinkedToAward() : ValidationError(numberError = "10.4.5.2", description = "Tenderer is not linked to award.")

    class DuplicateRequirementResponse() : ValidationError(numberError = "10.4.5.3", description = "Duplicate requirement response.")

}