package com.procurement.evaluation.service

import com.procurement.evaluation.application.service.award.AwardCancellationContext
import com.procurement.evaluation.application.service.award.AwardCancellationData
import com.procurement.evaluation.application.service.award.AwardCancelledData
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
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.bpe.pmd
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Phase.AWARDING
import com.procurement.evaluation.model.dto.ocds.Phase.CLARIFICATION
import com.procurement.evaluation.model.dto.ocds.Phase.EMPTY
import com.procurement.evaluation.model.dto.ocds.Phase.NEGOTIATION
import com.procurement.evaluation.model.dto.ocds.Phase.TENDERING
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.localNowUTC
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

    fun setFinalStatuses(cm: CommandMessage): ResponseDto {
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
        return ResponseDto(data = FinalStatusesRs(
                awards = awardsRs,
                awardPeriod = awardPeriod))
    }

    fun awardsCancellation(context: AwardCancellationContext, data: AwardCancellationData): AwardCancelledData {
        when (context.phase) {
            AWARDING -> {
                val awardEntities = awardDao.findAllByCpIdAndStage(cpId = context.cpid, stage = context.stage)
                if (awardEntities.isEmpty()) return AwardCancelledData(awards = emptyList())
                val awards: List<Award> = getAwardsFromEntities(awardEntities)
                val awardPredicate = getAwardPredicateForCancellation()

                val updatedAwards = awards.asSequence()
                    .filter(awardPredicate)
                    .map { award ->
                        award.copy(
                            date = context.startDate,
                            status = AwardStatus.UNSUCCESSFUL,
                            statusDetails = AwardStatusDetails.EMPTY
                        )
                    }
                    .toList()

                val newAwards: List<Award> = getUnsuccessfulAwards(data.lots)
                val persistentAwards = mergeUpdatedAwards(original = awards, updated = updatedAwards) + newAwards

                awardDao.saveAll(getUpdatedAwardEntities(awardEntities, persistentAwards))

                return AwardCancelledData(
                    awards = (updatedAwards + newAwards).map { award ->
                        AwardCancelledData.Award(
                            id = UUID.fromString(award.id),
                            title = award.title,
                            description = award.description,
                            date = award.date,
                            status = award.status,
                            statusDetails = award.statusDetails,
                            relatedLots = award.relatedLots.map { UUID.fromString(it) }
                        )
                    }
                )
            }
            TENDERING, CLARIFICATION, NEGOTIATION, EMPTY -> {
                val updatedAwards = getUnsuccessfulAwards(data.lots)
                awardDao.saveAll(getAwardEntities(updatedAwards, context.cpid, context.owner, context.stage))
                return AwardCancelledData(
                    awards = updatedAwards.map { award ->
                        AwardCancelledData.Award(
                            id = UUID.fromString(award.id),
                            title = award.title,
                            description = award.description,
                            date = award.date,
                            status = award.status,
                            statusDetails = award.statusDetails,
                            relatedLots = award.relatedLots.map { UUID.fromString(it) }
                        )
                    }
                )
            }
        }
    }

    private fun mergeUpdatedAwards(original: List<Award>, updated: List<Award>): List<Award> {
        val updatedAwardsById = updated.associateBy { it.id }
        return original.map { award ->
            updatedAwardsById[award.id] ?: award
        }
    }

    fun checkAwardValue(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val dto = toObject(CheckAwardRq::class.java, cm.data)

        val awardRq = dto.award
        val awardEntities = awardDao.findAllByCpId(cpId)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)

        val awards = getAwardsFromEntities(awardEntities)
        val award = awards.asSequence().firstOrNull { it.id == awardRq.id } ?: throw ErrorException(DATA_NOT_FOUND)
        if (awardRq.value.amountNet > award.value!!.amount) throw ErrorException(ErrorType.AMOUNT)
        if (awardRq.value.currency != award.value.currency) throw ErrorException(ErrorType.CURRENCY)
        return ResponseDto(data = "ok")
    }

    fun getAwardForCan(cm: CommandMessage): ResponseDto {
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

        return ResponseDto(data = GetAwardForCanRs(
                awardingSuccess = awardingSuccess,
                awardId = awardId))
    }

    fun getAwardIdForCheck(cm: CommandMessage): ResponseDto {
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

        return ResponseDto(data = AwardForCansRs(award.id))
    }

    fun getAwardsForAc(cm: CommandMessage): ResponseDto {
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
        return ResponseDto(data = AwardsForAcRs(awards))
    }

    fun endAwardPeriod(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = getStage(cm.pmd)
        val endDate = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val awardPeriod = periodService.saveEndOfPeriod(cpId, stage, endDate)
        return ResponseDto(data = EndAwardPeriodRs(awardPeriod))
    }

    fun getLotForCheck(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val awardEntity = awardDao.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token))
        if (awardEntity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        val awardByBid = toObject(Award::class.java, awardEntity.jsonData)
        return ResponseDto(data = GetLotForCheckRs(awardByBid.relatedLots[0]))
    }

    private fun getUnsuccessfulAwards(unSuccessfulLots: List<AwardCancellationData.Lot>): List<Award> {
        return unSuccessfulLots.asSequence()
            .map { lot ->
                generateUnsuccessfulAward(lotId = lot.id)
            }
            .toList()
    }

    private fun generateUnsuccessfulAward(lotId: String): Award = Award(
        token = generationService.token().toString(),
        id = generationService.awardId().toString(),
        date = localNowUTC(),
        description = "Other reasons (discontinuation of procedure)",
        title = "The contract/lot is not awarded",
        status = AwardStatus.UNSUCCESSFUL,
        statusDetails = AwardStatusDetails.EMPTY,
        value = null,
        relatedLots = listOf(lotId),
        relatedBid = null,
        bidDate = null,
        suppliers = null,
        documents = null,
        items = null,
        weightedValue = null
    )

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

    private fun getUnsuccessfulLotsFromAwards(awards: List<Award>): List<Lot> {
        val successfulLots = awards.asSequence()
                .filter { it.status == AwardStatus.ACTIVE }
                .flatMap { it.relatedLots.asSequence() }
                .toList()
        val unsuccessfulLots = awards.asSequence()
                .filter { it.status == AwardStatus.UNSUCCESSFUL }
                .flatMap { it.relatedLots.asSequence() }
                .filter { lot -> !successfulLots.contains(lot) }.toHashSet()
        return unsuccessfulLots.asSequence().map { Lot(it) }.toList()
    }

    private fun getAwardPredicateForPrepareCancellation(): (Award) -> Boolean {
        return { award: Award ->
            (award.status == AwardStatus.PENDING)
                    && (award.statusDetails == AwardStatusDetails.EMPTY
                    || award.statusDetails == AwardStatusDetails.ACTIVE
                    || award.statusDetails == AwardStatusDetails.CONSIDERATION)
        }
    }

    private fun getAwardPredicateForCancellation(): (Award) -> Boolean {
        return { award: Award ->
            (award.status == AwardStatus.PENDING) && (award.statusDetails == AwardStatusDetails.UNSUCCESSFUL)
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

    private fun getAwardEntities(awards: List<Award>, cpId: String, owner: String, stage: String): List<AwardEntity> {
        val entities = ArrayList<AwardEntity>()
        awards.asSequence()
                .forEach { award ->
                    entities.add(getEntity(
                            award = award,
                            cpId = cpId,
                            stage = stage,
                            owner = owner,
                            token = UUID.fromString(award.token)))
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
                status = award.status.value,
                statusDetails = award.statusDetails.value,
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
