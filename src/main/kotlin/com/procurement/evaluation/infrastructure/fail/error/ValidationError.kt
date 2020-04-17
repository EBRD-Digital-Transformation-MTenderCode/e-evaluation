package com.procurement.evaluation.infrastructure.fail.error

import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.infrastructure.fail.Fail

sealed class ValidationError(
    numberError: String, override val description: String, val id: String? = null
) : Fail.Error("VR-") {
    override val code: String = prefix + numberError

    class InvalidToken() : ValidationError(
        numberError = "10.4.2.1",
        description = "Request token doesn't match token from the database."
    )

    class InvalidOwner() : ValidationError(
        numberError = "10.4.2.2",
        description = "Request owner doesn't match owner from the database."
    )

    class AwardNotFoundOnCheckRelatedTenderer(id: AwardId) : ValidationError(
        numberError = "10.4.4.1",
        description = "Award not found.",
        id = id.toString()
    )

    class TendererNotLinkedToAwardOnCheckRelatedTenderer : ValidationError(
        numberError = "10.4.4.2",
        description = "Tenderer is not linked to award."
    )

    class DuplicateRequirementResponseOnCheckRelatedTenderer : ValidationError(
        numberError = "10.4.4.3",
        description = "Duplicate requirement response."
    )

    class AwardNotFoundOnCheckAccess(id: AwardId) : ValidationError(
        numberError = "10.4.2.3",
        description = "Award not found.",
        id = id.toString()
    )

    class AwardNotFoundOnCreateRequirementRs(id: AwardId) : ValidationError(
        numberError = "10.4.4.1",
        description = "Award not found.",
        id = id.toString()
    )

    class AwardNotFoundOnGetAwardState(id: AwardId) : ValidationError(
        numberError = "10.4.1.1",
        description = "Award not found.",
        id = id.toString()
    )

    class PeriodNotFoundOnCloseAwardPeriod() : ValidationError(
        numberError = "10.4.6.1",
        description = "Period not found."
    )
}