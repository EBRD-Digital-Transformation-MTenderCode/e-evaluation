package com.procurement.evaluation.application.service

import com.procurement.evaluation.application.repository.award.AwardRepository
import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.exception.ErrorType.CONTEXT
import com.procurement.evaluation.exception.ErrorType.DATA_NOT_FOUND
import com.procurement.evaluation.infrastructure.api.v1.CommandMessage
import com.procurement.evaluation.infrastructure.api.v1.cpid
import com.procurement.evaluation.infrastructure.api.v1.ocid
import com.procurement.evaluation.infrastructure.api.v1.owner
import com.procurement.evaluation.infrastructure.api.v1.startDate
import com.procurement.evaluation.infrastructure.api.v1.token
import com.procurement.evaluation.infrastructure.handler.v1.model.request.AwardsForAcRq
import com.procurement.evaluation.infrastructure.handler.v1.model.request.AwardsForAcRs
import com.procurement.evaluation.infrastructure.handler.v1.model.response.AwardForCansRs
import com.procurement.evaluation.infrastructure.handler.v1.model.response.EndAwardPeriodRs
import com.procurement.evaluation.infrastructure.handler.v1.model.response.GetLotForCheckRs
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service

@Service
class StatusService(
    private val periodService: PeriodService,
    private val awardRepository: AwardRepository
) {

    fun getAwardIdForCheck(cm: CommandMessage): AwardForCansRs {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val bidId = cm.context.id ?: throw ErrorException(CONTEXT)

        val awardEntities = awardRepository.findBy(cpid, ocid)
            .orThrow { it.exception }

        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)

        val awards = getAwardsFromEntities(awardEntities)
        val award = awards
            .firstOrNull { it.relatedBid == bidId }
            ?: throw ErrorException(DATA_NOT_FOUND)

        return AwardForCansRs(award.id)
    }

    fun getAwardsForAc(cm: CommandMessage): AwardsForAcRs {
        val cpid = cm.cpid
        val ocid = cm.ocid

        val dto = toObject(AwardsForAcRq::class.java, cm.data)
        val awardsIdsSet = dto.cans.asSequence().map { it.awardId }.toSet()

        val awardEntities = awardRepository.findBy(cpid, ocid)
            .orThrow { it.exception }

        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)

        val awards = getAwardsFromEntities(awardEntities)
                .asSequence()
                .filter { awardsIdsSet.contains(it.id) }
                .toList()
        return AwardsForAcRs(awards)
    }

    fun endAwardPeriod(cm: CommandMessage): EndAwardPeriodRs {
        val cpid = cm.cpid
        val endDate = cm.startDate
        val awardPeriod = periodService.saveEndOfPeriod(cpid, endDate)
        return EndAwardPeriodRs(awardPeriod)
    }

    fun getLotForCheck(cm: CommandMessage): GetLotForCheckRs {
        val cpid = cm.cpid
        val ocid = cm.ocid
        val token = cm.token
        val owner = cm.owner

        val awardEntity = awardRepository.findBy(cpid, ocid, token)
            .orThrow { it.exception }
            ?: throw ErrorException(DATA_NOT_FOUND)

        if (awardEntity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        val awardByBid = toObject(Award::class.java, awardEntity.jsonData)
        return GetLotForCheckRs(awardByBid.relatedLots[0])
    }

    private fun getAwardsFromEntities(awardEntities: List<AwardEntity>): List<Award> {
        return awardEntities.asSequence().map { toObject(Award::class.java, it.jsonData) }.toList()
    }

}
