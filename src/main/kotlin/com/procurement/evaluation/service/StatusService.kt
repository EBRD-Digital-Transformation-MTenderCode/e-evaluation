package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType.CONTEXT
import com.procurement.evaluation.exception.ErrorType.DATA_NOT_FOUND
import com.procurement.evaluation.model.dto.AwardCancellation
import com.procurement.evaluation.model.dto.CancellationRq
import com.procurement.evaluation.model.dto.CancellationRs
import com.procurement.evaluation.model.dto.FinalStatusesRs
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Phase
import com.procurement.evaluation.model.dto.ocds.Phase.*
import com.procurement.evaluation.model.dto.ocds.Status
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
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val endDate = cm.context.endDate?.toLocal() ?: throw ErrorException(CONTEXT)

        val awardPeriod = periodService.saveEndOfPeriod(cpId, stage, endDate)
        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)
        val awards = getAwardsFromEntities(awardEntities)
        setAwardsStatusFromStatusDetails(awards, endDate)
        awardDao.saveAll(getUpdatedAwardEntities(awardEntities, awards))
        val unsuccessfulLots = getUnsuccessfulLotsFromAwards(awards)
        return ResponseDto(data = FinalStatusesRs(awards, awardPeriod, unsuccessfulLots))
    }

    fun prepareCancellation(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)

        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        if (awardEntities.isEmpty()) return ResponseDto(data = CancellationRs(listOf()))
        val awards = getAwardsFromEntities(awardEntities)
        val awardPredicate = getAwardPredicateForPrepareCancellation()
        val awardsResponseDto = mutableListOf<AwardCancellation>()
        awards.asSequence()
                .filter(awardPredicate)
                .forEach { award ->
                    award.date = dateTime
                    award.statusDetails = Status.UNSUCCESSFUL
                    addAwardToResponseDto(awardsResponseDto, award)
                }
        awardDao.saveAll(getUpdatedAwardEntities(awardEntities, awards))
        return ResponseDto(data = CancellationRs(awardsResponseDto))
    }

    fun awardsCancellation(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val phase = cm.context.phase ?: throw ErrorException(CONTEXT)
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)

        val awardsResponseDto = mutableListOf<AwardCancellation>()
        when (Phase.fromValue(phase)) {
            AWARDING -> {
                val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
                if (awardEntities.isEmpty()) return ResponseDto(data = CancellationRs(listOf()))
                val awards = getAwardsFromEntities(awardEntities)
                val awardPredicate = getAwardPredicateForCancellation()
                awards.asSequence()
                        .filter(awardPredicate)
                        .forEach { award ->
                            award.date = dateTime
                            award.status = Status.UNSUCCESSFUL
                            award.statusDetails = Status.EMPTY
                            addAwardToResponseDto(awardsResponseDto, award)
                        }
                awardDao.saveAll(getUpdatedAwardEntities(awardEntities, awards))
            }
            TENDERING, CLARIFICATION, EMPTY -> {
                val dto = toObject(CancellationRq::class.java, cm.data)
                val awards = getUnsuccessfulAwards(dto.lots)
                awards.asSequence().forEach { award ->
                    addAwardToResponseDto(awardsResponseDto, award)
                }
                awardDao.saveAll(getAwardEntities(awards, cpId, owner, stage))
            }
        }
        return ResponseDto(data = CancellationRs(awardsResponseDto))
    }

    private fun getUnsuccessfulAwards(unSuccessfulLots: List<Lot>): List<Award> {
        return unSuccessfulLots.asSequence().map { lot ->
            Award(
                    token = generationService.generateRandomUUID().toString(),
                    id = generationService.getTimeBasedUUID(),
                    date = localNowUTC(),
                    description = "Other reasons (discontinuation of procedure)",
                    title = "The contract/lot is not awarded",
                    status = Status.UNSUCCESSFUL,
                    statusDetails = Status.EMPTY,
                    value = null,
                    relatedLots = listOf(lot.id),
                    relatedBid = null,
                    suppliers = null,
                    documents = null,
                    items = null)
        }.toList()
    }

    private fun getAwardsFromEntities(awardEntities: List<AwardEntity>): List<Award> {
        return awardEntities.asSequence().map { toObject(Award::class.java, it.jsonData) }.toList()
    }

    private fun setAwardsStatusFromStatusDetails(awards: List<Award>, endPeriod: LocalDateTime) {
        awards.forEach { award ->
            if (award.statusDetails != Status.EMPTY) {
                award.date = endPeriod
                award.status = award.statusDetails
                award.statusDetails = Status.EMPTY
            }
            if (award.status == Status.PENDING && award.statusDetails == Status.EMPTY) {
                award.date = endPeriod
                award.status = Status.UNSUCCESSFUL
            }
        }
    }

    private fun getUnsuccessfulLotsFromAwards(awards: List<Award>): List<Lot> {
        val successfulLots = awards.asSequence()
                .filter { it.status == Status.ACTIVE }
                .flatMap { it.relatedLots.asSequence() }
                .toList()
        val unsuccessfulLots = awards.asSequence()
                .filter { it.status == Status.UNSUCCESSFUL }
                .flatMap { it.relatedLots.asSequence() }
                .filter { lot -> !successfulLots.contains(lot) }.toHashSet()
        return unsuccessfulLots.asSequence().map { Lot(it) }.toList()
    }

    private fun getAwardPredicateForPrepareCancellation(): (Award) -> Boolean {
        return { award: Award ->
            (award.status == Status.PENDING)
                    && (award.statusDetails == Status.EMPTY
                    || award.statusDetails == Status.ACTIVE
                    || award.statusDetails == Status.CONSIDERATION)
        }
    }

    private fun getAwardPredicateForCancellation(): (Award) -> Boolean {
        return { award: Award ->
            (award.status == Status.PENDING) && (award.statusDetails == Status.UNSUCCESSFUL)
        }
    }

    private fun addAwardToResponseDto(awardsResponseDto: MutableList<AwardCancellation>, award: Award) {
        awardsResponseDto.add(AwardCancellation(
                id = award.id,
                status = award.status,
                statusDetails = award.statusDetails))
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
                status = award.status.value(),
                statusDetails = award.statusDetails.value(),
                owner = owner,
                jsonData = toJson(award))
    }

}
