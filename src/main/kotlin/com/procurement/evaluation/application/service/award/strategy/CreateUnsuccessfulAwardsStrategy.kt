package com.procurement.evaluation.application.service.award.strategy

import com.procurement.evaluation.application.model.award.unsuccessful.CreateUnsuccessfulAwardsParams
import com.procurement.evaluation.application.repository.AwardRepository
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asFailure
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.create.unsuccessfulaward.CreateUnsuccessfulAwardsResult
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.service.GenerationService
import com.procurement.evaluation.utils.toJson

class CreateUnsuccessfulAwardsStrategy(
    val awardRepository: AwardRepository,
    val generationService: GenerationService
) {

    fun execute(params: CreateUnsuccessfulAwardsParams): Result<List<CreateUnsuccessfulAwardsResult>, Fail> {

        val token = generationService.token()

        /**FR-10.4.5.1*/
        val awards = params.lotIds
            .map { lotId ->
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
                    statusDetails = AwardStatusDetails.LOT_CANCELLED,
                    /*FR-10.4.5.8*/
                    relatedLots = listOf(lotId.toString()),

                    token = token.toString(),
                    value = null,
                    bidDate = null,
                    documents = null,
                    items = null,
                    relatedBid = null,
                    suppliers = null,
                    weightedValue = null
                )
            }

        val awardEntities = awards
            .map { award ->
                AwardEntity(
                    cpId = params.cpid.toString(),
                    stage = params.ocid.stage.toString(),
                    token = token,
                    status = award.status.toString(),
                    statusDetails = award.statusDetails.toString(),
                    owner = null,
                    jsonData = toJson(award)
                )
            }
        awardRepository.trySave(cpid = params.cpid, awards = awardEntities)
            .doReturn { error -> return error.asFailure() }

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
}
