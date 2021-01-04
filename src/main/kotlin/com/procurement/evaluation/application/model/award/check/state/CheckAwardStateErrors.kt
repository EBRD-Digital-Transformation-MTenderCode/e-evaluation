package com.procurement.evaluation.application.model.award.check.state

import com.procurement.evaluation.domain.model.state.States
import com.procurement.evaluation.infrastructure.fail.Failure

sealed class CheckAwardStateErrors(
    numberError: String, override val description: String, val id: String? = null
) : Failure.Error("VR.COM-") {
    override val code: String = prefix + numberError

    class MissingAwardsAttribute : CheckAwardStateErrors(
        numberError = "4.11.1",
        description = "Missing 'awards' attribute in request."
    )

    class MissingLotsAttribute : CheckAwardStateErrors(
        numberError = "4.11.2",
        description = "Missing 'tender.lots' attribute in request."
    )

    class MissingAwardById(awardIds: List<String>) : CheckAwardStateErrors(
        numberError = "4.11.3",
        description = "Cannot find awards from request. Ids: ${awardIds}"
    )

    class MissingAwardByRelatedLot(relatedLots: List<String>) : CheckAwardStateErrors(
        numberError = "4.11.4",
        description = "Cannot find awards by related lots specified in request. Ids: ${relatedLots}"
    )


    class InvalidAwardState(awardId: String, state: States.State) : CheckAwardStateErrors(
        numberError = "4.11.2",
        description = "Award '$awardId' has invalid state: $state."
    )
}