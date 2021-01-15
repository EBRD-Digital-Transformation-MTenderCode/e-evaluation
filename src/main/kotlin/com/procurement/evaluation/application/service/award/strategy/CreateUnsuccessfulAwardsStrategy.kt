package com.procurement.evaluation.application.service.award.strategy

import com.procurement.evaluation.application.model.award.unsuccessful.CreateUnsuccessfulAwardsParams
import com.procurement.evaluation.application.repository.award.AwardRepository
import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.application.service.GenerationService
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.enums.EnumElementProvider.Companion.keysAsStrings
import com.procurement.evaluation.domain.model.enums.OperationType2
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.response.CreateUnsuccessfulAwardsResult
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.Result.Companion.failure
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.utils.toJson

class CreateUnsuccessfulAwardsStrategy(
    val awardRepository: AwardRepository,
    val generationService: GenerationService
) {

    fun execute(params: CreateUnsuccessfulAwardsParams): Result<List<CreateUnsuccessfulAwardsResult>, Failure> {

        val token = generationService.token()

        /**FR-10.4.5.1*/
        val awards = params.lotIds
            .map { lotId ->
                val statusDetails = defineAwardStatusDetails(params.operationType)
                    .onFailure { return it }
                Award(
                    /*FR-10.4.5.2*/
                    id = generationService.awardId().toString(),
                    /*FR-10.4.5.3*/
                    date = params.date,
                    /*FR-10.4.5.4*/
                    title = "Lot is not awarded",
                    /*FR-10.4.5.5*/
                    description = "Other reasons (discontinuation of procedure)",
                    /*FR-10.4.5.6*/
                    status = AwardStatus.UNSUCCESSFUL,
                    /*FR-10.4.5.7*/
                    statusDetails = statusDetails,
                    /*FR-10.4.5.8*/
                    relatedLots = listOf(lotId.toString()),

                    token = token.toString(),
                    value = null,
                    bidDate = null,
                    documents = null,
                    items = null,
                    relatedBid = null,
                    suppliers = null,
                    weightedValue = null,
                    internalId = null
                )
            }

        val awardEntities = awards
            .map { award ->
                AwardEntity(
                    cpid = params.cpid,
                    ocid = params.ocid,
                    token = token,
                    status = award.status,
                    statusDetails = award.statusDetails,
                    owner = null,
                    jsonData = toJson(award)
                )
            }

        val wasApplied = awardRepository.save(cpid = params.cpid, awards = awardEntities)
            .onFailure { return it }

        if (!wasApplied)
            return failure(
                Failure.Incident.Database.RecordIsNotExist(description = "An error occurred when writing a record(s) of the awards by cpid '${params.cpid}' to the database. Record(s) is not exists.")
            )

        return awards.map { award ->
            award.toResult()
        }
            .asSuccess()
    }

    private fun Award.toResult(): CreateUnsuccessfulAwardsResult = CreateUnsuccessfulAwardsResult(
        id = AwardId.fromString(id),
        status = status,
        statusDetails = statusDetails,
        relatedLots = relatedLots.map { LotId.fromString(it) },
        title = title!!,
        description = description!!,
        date = date!!
    )

    private fun defineAwardStatusDetails(operationType: OperationType2): Result<AwardStatusDetails, DataErrors.Validation.UnknownValue> =
        when (operationType) {
            OperationType2.APPLY_QUALIFICATION_PROTOCOL -> AwardStatusDetails.LACK_OF_QUALIFICATIONS.asSuccess()
            OperationType2.SUBMISSION_PERIOD_END -> AwardStatusDetails.LACK_OF_SUBMISSIONS.asSuccess()
            OperationType2.TENDER_OR_LOT_AMENDMENT_CONFIRMATION -> AwardStatusDetails.LOT_CANCELLED.asSuccess()

            OperationType2.CREATE_AWARD,
            OperationType2.CREATE_PCR,
            OperationType2.CREATE_SUBMISSION,
            OperationType2.DECLARE_NON_CONFLICT_OF_INTEREST,
            OperationType2.LOT_CANCELLATION,
            OperationType2.PCR_PROTOCOL,
            OperationType2.TENDER_CANCELLATION,
            OperationType2.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
            OperationType2.UPDATE_AWARD ->
                failure(
                    DataErrors.Validation.UnknownValue(
                        name = "operationType",
                        expectedValues = CreateUnsuccessfulAwardsParams.allowedOperationTypes.keysAsStrings(),
                        actualValue = operationType.toString()
                    )
                )
        }
}
