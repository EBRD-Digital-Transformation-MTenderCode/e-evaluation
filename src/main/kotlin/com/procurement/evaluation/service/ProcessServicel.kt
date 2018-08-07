package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.model.dto.AwardUpdate
import com.procurement.evaluation.model.dto.AwardsResponseDto
import com.procurement.evaluation.model.dto.UpdateAwardRequestDto
import com.procurement.evaluation.model.dto.UpdateAwardResponseDto
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Status
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

interface ProcessService {

    fun updateAndGetNextAward(cpId: String,
                              stage: String,
                              token: String,
                              owner: String,
                              dateTime: LocalDateTime,
                              dto: UpdateAwardRequestDto): ResponseDto

    fun endAwardPeriod(cpId: String,
                       stage: String,
                       country: String,
                       pmd: String,
                       endPeriod: LocalDateTime): ResponseDto

}

@Service
class ProcessServiceImpl(private val awardDao: AwardDao,
                         private val periodService: PeriodService) : ProcessService {

    override fun updateAndGetNextAward(cpId: String,
                                       stage: String,
                                       token: String,
                                       owner: String,
                                       dateTime: LocalDateTime,
                                       dto: UpdateAwardRequestDto): ResponseDto {
        val awardDto = dto.award
        when (awardDto.statusDetails) {
            Status.ACTIVE -> {
                val entity = awardDao.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token))
                if (entity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
                if (entity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
                val award = toObject(Award::class.java, entity.jsonData)
                updateActiveAward(award, awardDto, dateTime)
                val newEntity = getEntity(
                        award = award,
                        cpId = cpId,
                        token = entity.token,
                        stage = stage,
                        owner = owner)
                awardDao.save(newEntity)
                return getResponseDtoForActiveAward(award)
            }
            Status.UNSUCCESSFUL -> {
                val entities = awardDao.findAllByCpIdAndStage(cpId, stage)
                val awardToEntityToMap = getAwardToEntityToMap(entities)
                return updateUnsuccessfulAward(awardDto, awardToEntityToMap, dateTime)
            }
            else -> throw ErrorException(ErrorType.INVALID_STATUS_DETAILS)
        }
    }

    override fun endAwardPeriod(cpId: String,
                                stage: String,
                                country: String,
                                pmd: String,
                                endPeriod: LocalDateTime): ResponseDto {
        val awardPeriod = periodService.saveEndOfPeriod(cpId, stage, endPeriod)
        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        val awards = getAwardsFromEntities(awardEntities)
        setAwardsStatusFromStatusDetails(awards, endPeriod)
        val unsuccessfulLots = getUnsuccessfulLotsFromAwards(awards)
        return ResponseDto(true, null, AwardsResponseDto(awards, awardPeriod, unsuccessfulLots))
    }

    private fun updateUnsuccessfulAward(awardDto: AwardUpdate,
                                        awardsFromEntities: Map<Award, AwardEntity>,
                                        dateTime: LocalDateTime): ResponseDto {
        val updatableAward = awardsFromEntities.keys.asSequence()
                .firstOrNull { it.id == awardDto.id }
                ?: throw  ErrorException(ErrorType.DATA_NOT_FOUND)

        val updatedAwardEntity = awardsFromEntities[updatableAward] ?: throw  ErrorException(ErrorType.DATA_NOT_FOUND)

        updatableAward.statusDetails = Status.UNSUCCESSFUL
        if (awardDto.description != null) updatableAward.description = awardDto.description
        if (awardDto.documents != null) updatableAward.documents = awardDto.documents

        updatableAward.date = dateTime
        updatedAwardEntity.statusDetails = updatableAward.statusDetails.value()
        updatedAwardEntity.jsonData = toJson(updatableAward)
        awardDao.save(updatedAwardEntity)

        // next Award
        val awardsByLot = awardsFromEntities.keys.asSequence()
                .filter { it.relatedLots == updatableAward.relatedLots }.toList()
        val sortedAwardsByLot = awardsByLot.asSequence().sortedWith(SortedByValue).toList()
        var nextAwardByLot: Award? = null
        if (sortedAwardsByLot.size > 1) {
            nextAwardByLot = sortedAwardsByLot.asSequence()
                    .firstOrNull { it.id != updatableAward.id && it.statusDetails != Status.UNSUCCESSFUL }
            if (nextAwardByLot != null) {
                val nextAwardByLotEntity = awardsFromEntities[nextAwardByLot]
                        ?: throw  ErrorException(ErrorType.DATA_NOT_FOUND)
                nextAwardByLot.statusDetails = Status.CONSIDERATION
                nextAwardByLot.date = dateTime
                nextAwardByLotEntity.statusDetails = nextAwardByLot.statusDetails.value()
                nextAwardByLotEntity.jsonData = toJson(nextAwardByLot)
                awardDao.save(nextAwardByLotEntity)
            }
        }
        return ResponseDto(true, null, UpdateAwardResponseDto(updatableAward, nextAwardByLot))
    }

    private fun updateActiveAward(award: Award, awardDto: AwardUpdate, dateTime: LocalDateTime) {
        if (award.statusDetails != Status.CONSIDERATION) throw ErrorException(ErrorType.INVALID_STATUS_DETAILS)
        if (awardDto.description != null) award.description = awardDto.description
        if (awardDto.documents != null) award.documents = awardDto.documents
        award.date = dateTime
        award.statusDetails = Status.ACTIVE
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

    private fun getAwardToEntityToMap(awardEntities: List<AwardEntity>): Map<Award, AwardEntity> {
        return awardEntities.map { toObject(Award::class.java, it.jsonData) to it }.toMap()
    }

    private fun getResponseDtoForActiveAward(award: Award): ResponseDto {
        return ResponseDto(true, null, UpdateAwardResponseDto(award = award, nextAward = null))
    }

    companion object SortedByValue : Comparator<Award> {
        override fun compare(a: Award, b: Award): Int {
            return a.value!!.amount.compareTo(b.value!!.amount)
        }
    }

    private fun getEntity(award: Award,
                          cpId: String,
                          stage: String,
                          token: UUID,
                          owner: String): AwardEntity {
        val status = award.status ?: throw ErrorException(ErrorType.INVALID_STATUS)
        return AwardEntity(
                cpId = cpId,
                stage = stage,
                token = token,
                status = status.value(),
                statusDetails = award.statusDetails.value(),
                owner = owner,
                jsonData = toJson(award))
    }
}
