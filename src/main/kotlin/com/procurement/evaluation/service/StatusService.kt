package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.exception.ErrorType.CONTEXT
import com.procurement.evaluation.exception.ErrorType.DATA_NOT_FOUND
import com.procurement.evaluation.model.dto.AwardForCansRs
import com.procurement.evaluation.model.dto.AwardsForAcRq
import com.procurement.evaluation.model.dto.AwardsForAcRs
import com.procurement.evaluation.model.dto.CheckAwardRq
import com.procurement.evaluation.model.dto.EndAwardPeriodRs
import com.procurement.evaluation.model.dto.FinalAward
import com.procurement.evaluation.model.dto.FinalStatusesRs
import com.procurement.evaluation.model.dto.GetAwardForCanRs
import com.procurement.evaluation.model.dto.GetLotForCheckRs
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.pmd
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toLocal
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class StatusService(private val periodService: PeriodService,
                    private val generationService: GenerationService,
                    private val awardDao: AwardDao) {

    fun setFinalStatuses(cm: CommandMessage): FinalStatusesRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val endDate = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val stage = getStage(cm.pmd)
        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)
        val awards = getAwardsFromEntities(awardEntities)
        setAwardsStatusFromStatusDetails(awards, endDate)
        val awardPeriod = periodService.saveEndOfPeriod(cpId, stage, endDate)
        awardDao.saveAll(getUpdatedAwardEntities(awardEntities, awards))
        val awardsRs = awards.asSequence()
                .map { FinalAward(id = it.id, status = it.status, statusDetails = it.statusDetails) }
                .toList()
        return FinalStatusesRs(
                awards = awardsRs,
                awardPeriod = awardPeriod
        )

    }

    fun checkAwardValue(cm: CommandMessage): String {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val dto = toObject(CheckAwardRq::class.java, cm.data)

        val awardRq = dto.award
        val awardEntities = awardDao.findAllByCpId(cpId)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)

        val awards = getAwardsFromEntities(awardEntities)
        val award = awards.asSequence().firstOrNull { it.id == awardRq.id } ?: throw ErrorException(DATA_NOT_FOUND)
        if (awardRq.value.amountNet > award.value!!.amount) throw ErrorException(ErrorType.AMOUNT)
        if (awardRq.value.currency != award.value.currency) throw ErrorException(ErrorType.CURRENCY)
        return "ok"
    }

    fun getAwardForCan(cm: CommandMessage): GetAwardForCanRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val lotId = cm.context.id ?: throw ErrorException(CONTEXT)
        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)

        val awardsFromEntities = getAwardsFromEntities(awardEntities)
        val awards = awardsFromEntities.asSequence().filter {
            it.relatedLots.contains(lotId)
                    && it.status == AwardStatus.PENDING
        }.toSet()

        var awardId: String? = null
        var awardingSuccess = false

        awards.forEach {
            if (it.statusDetails == AwardStatusDetails.ACTIVE) {
                awardId = it.id
                awardingSuccess = true
            }
        }

        return GetAwardForCanRs(awardingSuccess = awardingSuccess, awardId = awardId)
    }

    fun getAwardIdForCheck(cm: CommandMessage): AwardForCansRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val bidId = cm.context.id ?: throw ErrorException(CONTEXT)

        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)

        val awards = getAwardsFromEntities(awardEntities)
        val award = awards.asSequence().firstOrNull {
            it.relatedBid == bidId
        }
                ?: throw ErrorException(DATA_NOT_FOUND)

        return AwardForCansRs(award.id)
    }

    fun getAwardsForAc(cm: CommandMessage): AwardsForAcRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val dto = toObject(AwardsForAcRq::class.java, cm.data)
        val awardsIdsSet = dto.cans.asSequence().map { it.awardId }.toSet()

        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)

        val awards = getAwardsFromEntities(awardEntities)
                .asSequence()
                .filter { awardsIdsSet.contains(it.id) }
                .toList()
        return AwardsForAcRs(awards)
    }

    fun endAwardPeriod(cm: CommandMessage): EndAwardPeriodRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = getStage(cm.pmd)
        val endDate = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val awardPeriod = periodService.saveEndOfPeriod(cpId, stage, endDate)
        return EndAwardPeriodRs(awardPeriod)
    }

    fun getLotForCheck(cm: CommandMessage): GetLotForCheckRs {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val awardEntity = awardDao.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token))
        if (awardEntity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        val awardByBid = toObject(Award::class.java, awardEntity.jsonData)
        return GetLotForCheckRs(awardByBid.relatedLots[0])
    }

    private fun getAwardsFromEntities(awardEntities: List<AwardEntity>): List<Award> {
        return awardEntities.asSequence().map { toObject(Award::class.java, it.jsonData) }.toList()
    }

    private fun setAwardsStatusFromStatusDetails(awards: List<Award>, endPeriod: LocalDateTime) {
        awards.forEach { award ->
            if (award.status == AwardStatus.PENDING && award.statusDetails == AwardStatusDetails.ACTIVE) {
                award.date = endPeriod
                award.status = AwardStatus.ACTIVE
                award.statusDetails = AwardStatusDetails.EMPTY
            }
            if (award.status == AwardStatus.PENDING && award.statusDetails == AwardStatusDetails.UNSUCCESSFUL) {
                award.date = endPeriod
                award.status = AwardStatus.UNSUCCESSFUL
                award.statusDetails = AwardStatusDetails.EMPTY
            }
        }
    }

    private fun getUpdatedAwardEntities(awardEntities: List<AwardEntity>, awards: List<Award>): List<AwardEntity> {
        val entities = ArrayList<AwardEntity>()
        awardEntities.asSequence().forEach { entity ->
            awards.asSequence()
                    .firstOrNull { it.token == entity.token.toString() }
                    ?.let { award ->
                        entities.add(getEntity(
                                award = award,
                                cpId = entity.cpId,
                                stage = entity.stage,
                                owner = entity.owner,
                                token = entity.token))
                    }
        }
        return entities
    }

    private fun getEntity(award: Award,
                          cpId: String,
                          stage: String,
                          owner: String,
                          token: UUID): AwardEntity {
        return AwardEntity(
                cpId = cpId,
                stage = stage,
                token = token,
                status = award.status.key,
                statusDetails = award.statusDetails.key,
                owner = owner,
                jsonData = toJson(award))
    }

    private fun getStage(pmd: ProcurementMethod): String = when (pmd) {
        ProcurementMethod.OT, ProcurementMethod.TEST_OT,
        ProcurementMethod.SV, ProcurementMethod.TEST_SV,
        ProcurementMethod.MV, ProcurementMethod.TEST_MV -> "EV"

        ProcurementMethod.DA, ProcurementMethod.TEST_DA,
        ProcurementMethod.NP, ProcurementMethod.TEST_NP,
        ProcurementMethod.OP, ProcurementMethod.TEST_OP -> "NP"

        ProcurementMethod.RT, ProcurementMethod.TEST_RT,
        ProcurementMethod.FA, ProcurementMethod.TEST_FA -> throw ErrorException(ErrorType.INVALID_PMD)
    }
}
