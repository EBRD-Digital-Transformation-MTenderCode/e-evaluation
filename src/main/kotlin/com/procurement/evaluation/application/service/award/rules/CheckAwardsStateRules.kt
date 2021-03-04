package com.procurement.evaluation.application.service.award.rules

import com.procurement.evaluation.application.model.award.check.state.CheckAwardStateErrors
import com.procurement.evaluation.application.model.award.check.state.CheckAwardStateParams
import com.procurement.evaluation.domain.model.enums.OperationType2
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asFailure
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.model.dto.ocds.Award

object CheckAwardsStateRules {
    fun filterAwards(storedAwards: List<Award>, params: CheckAwardStateParams): Result<List<Award>, Failure> {
        when (params.operationType) {
            OperationType2.DECLARE_NON_CONFLICT_OF_INTEREST,
            OperationType2.UPDATE_AWARD -> {
                if (params.awards.isEmpty())
                    return CheckAwardStateErrors.MissingAwardsAttribute().asFailure()

                val receivedAwardsIds = params.awards.map { it.id }
                val targetAwards = storedAwards.filter { it.id in receivedAwardsIds }

                val targetAwardsIds = targetAwards.map { it.id }
                if (!targetAwardsIds.containsAll(receivedAwardsIds))
                    return CheckAwardStateErrors.MissingAwardById(receivedAwardsIds-targetAwardsIds).asFailure()

                return targetAwards.asSuccess()
            }
            OperationType2.PCR_PROTOCOL -> {
                if (params.tender == null || params.tender.lots.isEmpty())
                    return CheckAwardStateErrors.MissingLotsAttribute().asFailure()

                val receivedRelatedLots = params.tender.lots.map { it.id }
                val targetAwards = storedAwards.filter { it.relatedLots.any { it in receivedRelatedLots }  }

                val targetAwardsRelatedLots = targetAwards.map { it.relatedLots }.flatten()
                if (!targetAwardsRelatedLots.containsAll(receivedRelatedLots))
                    return CheckAwardStateErrors.MissingAwardByRelatedLot(receivedRelatedLots-targetAwardsRelatedLots).asFailure()

                return targetAwards.asSuccess()
            }

            OperationType2.APPLY_QUALIFICATION_PROTOCOL,
            OperationType2.CREATE_PCR,
            OperationType2.CREATE_AWARD,
            OperationType2.CREATE_SUBMISSION,
            OperationType2.LOT_CANCELLATION,
            OperationType2.SUBMISSION_PERIOD_END,
            OperationType2.TENDER_CANCELLATION,
            OperationType2.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
            OperationType2.TENDER_OR_LOT_AMENDMENT_CONFIRMATION ->
                return DataErrors.Validation.UnknownValue(
                    name = "operationType",
                    expectedValues = CheckAwardStateParams.allowedOperationTypes.map { it.key },
                    actualValue = params.operationType.key
                ).asFailure()
        }
    }


}