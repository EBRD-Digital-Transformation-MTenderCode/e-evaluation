package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.dao.PeriodDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.model.dto.AwardUpdate
import com.procurement.evaluation.model.dto.AwardsResponseDto
import com.procurement.evaluation.model.dto.UpdateAwardRequestDto
import com.procurement.evaluation.model.dto.UpdateAwardResponseDto
import com.procurement.evaluation.model.dto.awardByBid.AwardByBidRequestDto
import com.procurement.evaluation.model.dto.awardByBid.AwardByBidResponseDto
import com.procurement.evaluation.model.dto.awardByBid.AwardByBid
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.ocds.*
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
                              awardId: String,
                              owner: String,
                              dateTime: LocalDateTime,
                              dto: UpdateAwardRequestDto): ResponseDto

    fun endAwardPeriod(cpId: String,
                       stage: String,
                       country: String,
                       pmd: String,
                       endPeriod: LocalDateTime): ResponseDto

    fun awardByBid(cpId: String,
                   stage: String,
                   token: String,
                   awardId: String,
                   owner: String,
                   dateTime: LocalDateTime,
                   dto: AwardByBidRequestDto): ResponseDto

}

@Service
class ProcessServiceImpl(private val awardDao: AwardDao,
                         private val periodDao: PeriodDao,
                         private val periodService: PeriodService) : ProcessService {

    override fun updateAndGetNextAward(cpId: String,
                                       stage: String,
                                       token: String,
                                       awardId: String,
                                       owner: String,
                                       dateTime: LocalDateTime,
                                       dto: UpdateAwardRequestDto): ResponseDto {
        periodService.checkPeriod(cpId, stage)
        val awardDto = dto.award
        when (awardDto.statusDetails) {
            Status.ACTIVE -> {
                val entity = awardDao.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token))
                if (entity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)
                if (entity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
                val award = toObject(Award::class.java, entity.jsonData)
                if (award.id != awardId) throw ErrorException(ErrorType.INVALID_ID)
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
                return updateUnsuccessfulAward(awardDto, awardToEntityToMap, dateTime, token, awardId)
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

    override fun awardByBid(cpId: String,
                            stage: String,
                            token: String,
                            awardId: String,
                            owner: String,
                            dateTime: LocalDateTime,
                            dto: AwardByBidRequestDto): ResponseDto {

        val awardEntity = awardDao.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token))

        val award = toObject(Award::class.java, awardEntity.jsonData)
        val awardCriteria = AwardCriteria.fromValue(periodDao.getByCpIdAndStage(cpId,stage).awardCriteria)

        val relatedLots = award.relatedLots
        val documents = dto.awards.documents

        verifyDocumentsRelatedLots(relatedLots, documents)
        verifyAwardByBidStatusDetails(dto.awards.statusDetails)
        if (award.id != awardId) throw ErrorException(ErrorType.INVALID_ID)
        if (awardEntity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        verifyAwardByBidDocType(dto.awards.documents)

        val awardsByRelatedLots = getAwardsByRelatedLot(cpId, stage, relatedLots)
        val rangedAwards = sortAwardsByCriteria(awardsByRelatedLots,awardCriteria)

        var awardBidId: String? = null
        var awardStatusDetails: String? = null
        var awardLotId: String? = null
        var lotAwarded: Boolean? = null
        val awardsResponse = arrayListOf<Award>()


        if (dto.awards.statusDetails == Status.ACTIVE) {

            for (award in awardsByRelatedLots) {
                if (award.statusDetails == Status.ACTIVE) throw ErrorException(ErrorType.ALREADY_HAVE_ACTIVE_AWARDS)
            }

            if (award.statusDetails == Status.CONSIDERATION) {
                award.statusDetails = Status.ACTIVE
                updateAward(award, dto.awards, dateTime)
                if (award.relatedBid != null) {
                    awardBidId = award.relatedBid
                    awardStatusDetails = award.statusDetails.value()
                }

                lotAwarded = true
                awardLotId = award.relatedLots[0]

            } else if (award.statusDetails == Status.UNSUCCESSFUL) {
                if (isAlreadySavedAwardsFromStatus(awardsByRelatedLots, Status.CONSIDERATION)) {
                    award.statusDetails = Status.ACTIVE
                    updateAward(award, dto.awards, dateTime)
                    if (award.relatedBid != null) {
                        awardBidId = award.relatedBid
                        awardStatusDetails = award.statusDetails.value()
                    }
                    lotAwarded = true
                    awardLotId = award.relatedLots[0]

                    val awardsConsideration = getAlreadySavedAwardsFromStatus(awardsByRelatedLots, Status.CONSIDERATION)
                    for (awardConsideration in awardsConsideration) {
                        awardConsideration.statusDetails = Status.EMPTY
                        awardConsideration.date = dateTime
                        awardsResponse.add(awardConsideration)
                        val newEntity = getEntity(
                            award = awardConsideration,
                            cpId = cpId,
                            token = awardEntity.token,
                            stage = stage,
                            owner = owner)
                        awardDao.save(newEntity)
                    }

                } else {
                    award.statusDetails = Status.ACTIVE
                    updateAward(award, dto.awards, dateTime)
                    if (award.relatedBid != null) {
                        awardBidId = award.relatedBid
                        awardStatusDetails = award.statusDetails.value()

                    }
                }

            } else {
                throw ErrorException(ErrorType.INVALID_STATUS_DETAILS_SAVED_AWARD)
            }

        } else if (dto.awards.statusDetails == Status.UNSUCCESSFUL) {
            if (award.statusDetails == Status.CONSIDERATION) {
                val nextAwardAfterConsideration = getNextAwardAfterConsideration(rangedAwards)
                if (nextAwardAfterConsideration != null) {
                    award.statusDetails = Status.UNSUCCESSFUL
                    updateAward(award, dto.awards, dateTime)

                    if (award.relatedBid != null) {
                        awardBidId = award.relatedBid
                        awardStatusDetails = award.statusDetails.value()
                    }
                    nextAwardAfterConsideration.statusDetails = Status.CONSIDERATION
                    nextAwardAfterConsideration.date = dateTime
                    awardsResponse.add(nextAwardAfterConsideration)

                    val newEntity = getEntity(
                        award = nextAwardAfterConsideration,
                        cpId = awardEntity.cpId,
                        token = awardEntity.token,
                        stage = awardEntity.stage,
                        owner = awardEntity.owner)
                    awardDao.save(newEntity)
                } else {
                    award.statusDetails = Status.UNSUCCESSFUL
                    updateAward(award, dto.awards, dateTime)

                    if (award.relatedBid != null) {
                        awardBidId = award.relatedBid
                        awardStatusDetails = award.statusDetails.value()
                    }
                    lotAwarded = true
                    awardLotId = award.relatedLots.get(0)
                }


            } else if (award.statusDetails == Status.ACTIVE) {
                if (isAlreadySavedAwardsFromStatus(awardsByRelatedLots, Status.EMPTY)) {
                    award.statusDetails = Status.UNSUCCESSFUL
                    updateAward(award, dto.awards, dateTime)

                    if (award.relatedBid != null) {
                        awardBidId = award.relatedBid
                        awardStatusDetails = award.statusDetails.value()
                    }
                    lotAwarded = false
                    awardLotId = award.relatedLots.get(0)

                    val firsEmptyAward = getNextAwardAfterConsideration(rangedAwards)
                    if (firsEmptyAward != null) {
                        firsEmptyAward.statusDetails = Status.CONSIDERATION
                        firsEmptyAward.date = dateTime
                        awardsResponse.add(firsEmptyAward)

                        val newEntity = getEntity(
                            award = firsEmptyAward,
                            cpId = awardEntity.cpId,
                            token = awardEntity.token,
                            stage = awardEntity.stage,
                            owner = awardEntity.owner)
                        awardDao.save(newEntity)
                    }

                } else {
                    award.statusDetails = Status.UNSUCCESSFUL
                    updateAward(award, dto.awards, dateTime)
                    if (award.relatedBid != null) {
                        awardBidId = award.relatedBid
                        awardStatusDetails = award.statusDetails.value()
                    }

                }

            } else {
                throw ErrorException(ErrorType.INVALID_STATUS_DETAILS_SAVED_AWARD)
            }

        }
        awardsResponse.add(award)

        val newEntity = getEntity(
            award = award,
            cpId = awardEntity.cpId,
            token = awardEntity.token,
            stage = awardEntity.stage,
            owner = awardEntity.owner)
        awardDao.save(newEntity)

        return ResponseDto(true, null, AwardByBidResponseDto(
                awardsResponse,
                awardStatusDetails,
                awardBidId,
                awardLotId,
                lotAwarded))
    }

    private fun sortAwardsByCriteria(awards: List<Award>, awardCriteria: AwardCriteria): List<Award> {
        when (awardCriteria) {
            AwardCriteria.PRICE_ONLY -> {
                return awards.sortedBy { it.value?.amount }
            }
            AwardCriteria.COST_ONLY -> {
            }
            AwardCriteria.QUALITY_ONLY -> {
            }
            AwardCriteria.RATED_CRITERIA -> {
            }
            AwardCriteria.LOWEST_COST -> {
            }
            AwardCriteria.BEST_PROPOSAL -> {
            }
            AwardCriteria.BEST_VALUE_TO_GOVERNMENT -> {
            }
            AwardCriteria.SINGLE_BID_ONLY -> {
            }
        }
        return awards
    }

    private fun getNextAwardAfterConsideration(rangedAwards: List<Award>): Award? {
        for (i in 0 until rangedAwards.size - 1) {
            if (rangedAwards[i].statusDetails == Status.CONSIDERATION
                && rangedAwards[i + 1].statusDetails == Status.EMPTY) {
                return rangedAwards[i + 1]
            }
        }
        return null
    }

    private fun rangeAwardsByAmount(awards: List<Award>): List<Award> {
        return awards.sortedBy { it.value?.amount }
    }

    private fun updateAward(awardFromBase: Award, awardFromRq: AwardByBid, dateTime: LocalDateTime) {
        awardFromBase.apply {
            description = awardFromRq.description
            documents = awardFromRq.documents
            date = dateTime

        }
    }

    private fun getAwardsByRelatedLot(cpId: String, stage: String, relatedLots: List<String>): List<Award> {
        val awards = arrayListOf<Award>()
        val awardsSearchRelLots = getAwardsFromEntities(awardDao.findAllByCpIdAndStage(cpId, stage))
        for (awardInAwards in awardsSearchRelLots) {
            if (relatedLots.containsAll(awardInAwards.relatedLots))
                awards.add(awardInAwards)
        }
        return awards
    }

    private fun isAlreadySavedAwardsFromStatus(awardsByRelatedLots: List<Award>, statusDetails: Status): Boolean {
        val awards = getAlreadySavedAwardsFromStatus(awardsByRelatedLots, statusDetails)
        if (awards.isNotEmpty()) {
            return true
        }
        return false
    }

    private fun getAlreadySavedAwardsFromStatus(awardsByRelatedLots: List<Award>, statusDetails: Status): List<Award> {

        val outputAwards = arrayListOf<Award>()
        for (award in awardsByRelatedLots) {
            if (award.statusDetails == statusDetails) {
                outputAwards.add(award)
            }
        }
        return outputAwards
    }

    private fun verifyAwardByBidDocType(documents: List<Document>?) {

        val validTypes = arrayListOf<DocumentType>()
        validTypes.add(DocumentType.AWARD_NOTICE)
        validTypes.add(DocumentType.EVALUATION_REPORTS)
        validTypes.add(DocumentType.SHORTLISTED_FIRMS)
        validTypes.add(DocumentType.WINNING_BID)
        validTypes.add(DocumentType.COMPLAINTS)
        validTypes.add(DocumentType.BIDDERS)
        validTypes.add(DocumentType.CONFLICT_OF_INTEREST)
        validTypes.add(DocumentType.CANCELLATION_DETAILS)
        if (documents != null) {
            for (document in documents) {
                if (!validTypes.contains(document.documentType)) throw ErrorException(ErrorType.INVALID_DOC_TYPE)
            }
        }
    }

    private fun verifyAwardByBidStatusDetails(statusDetails: Status) {
        if (!(statusDetails == Status.ACTIVE || statusDetails == Status.UNSUCCESSFUL)) throw ErrorException(ErrorType.INVALID_STATUS_DETAILS)
    }

    private fun verifyDocumentsRelatedLots(relatedLots: List<String>, documents: List<Document>?) {
        if (documents != null) {
            for (document in documents) {
                val docRelatedLots = mutableListOf<String>()
                document.relatedLots?.toCollection(docRelatedLots)
                if (!relatedLots.containsAll(docRelatedLots)) throw ErrorException(ErrorType.RELATED_LOTS_IN_DOCS_ARE_INVALID)
            }
        }

    }

    private fun updateUnsuccessfulAward(awardDto: AwardUpdate,
                                        awardsFromEntities: Map<Award, AwardEntity>,
                                        dateTime: LocalDateTime,
                                        token: String,
                                        awardId: String): ResponseDto {
        val updatableAward = awardsFromEntities.keys.asSequence()
            .firstOrNull { it.id == awardId }
            ?: throw  ErrorException(ErrorType.DATA_NOT_FOUND)

        val updatedAwardEntity = awardsFromEntities[updatableAward] ?: throw  ErrorException(ErrorType.DATA_NOT_FOUND)
        if (updatedAwardEntity.token.toString() != token) throw ErrorException(ErrorType.INVALID_TOKEN)

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
