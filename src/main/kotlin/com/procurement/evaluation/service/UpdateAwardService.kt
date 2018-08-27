package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.dao.PeriodDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.model.dto.awardByBid.AwardByBid
import com.procurement.evaluation.model.dto.awardByBid.AwardByBidRequestDto
import com.procurement.evaluation.model.dto.awardByBid.AwardByBidResponseDto
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.ocds.*
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

interface UpdateAwardService {

    fun awardByBid(cpId: String,
                   stage: String,
                   token: String,
                   awardId: String,
                   owner: String,
                   dateTime: LocalDateTime,
                   dto: AwardByBidRequestDto): ResponseDto
}

@Service
class UpdateAwardServiceImpl(private val awardDao: AwardDao,
                             private val periodDao: PeriodDao) : UpdateAwardService {

    override fun awardByBid(cpId: String,
                            stage: String,
                            token: String,
                            awardId: String,
                            owner: String,
                            dateTime: LocalDateTime,
                            dto: AwardByBidRequestDto): ResponseDto {

        val awardEntity = awardDao.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token))
        if (awardEntity.owner != owner) throw ErrorException(ErrorType.INVALID_OWNER)
        val awardByBid = toObject(Award::class.java, awardEntity.jsonData)
        validation(awardByBid, awardId, dto)
        val awardCriteria = AwardCriteria.fromValue(periodDao.getByCpIdAndStage(cpId, stage).awardCriteria)

        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        val awardIdToEntityMap: MutableMap<String, AwardEntity> = mutableMapOf()
        val awardFromEntitiesSet: MutableSet<Award> = mutableSetOf()
        awardEntities.forEach { entity ->
            val awardFromEntity = toObject(Award::class.java, entity.jsonData)
            if (awardFromEntity.relatedLots == awardByBid.relatedLots) {
                awardIdToEntityMap[awardFromEntity.id] = entity
                awardFromEntitiesSet.add(awardFromEntity)
            }
        }
        val rangedAwards = sortAwardsByCriteria(awardFromEntitiesSet, awardCriteria)

        var bidId: String? = null
        var statusDetails: String? = null
        var lotId: String? = null
        var lotAwarded: Boolean? = null
        var nextAwardForUpdate: Award? = null

        /*********************ACTIVE*********************/
        if (dto.award.statusDetails == Status.ACTIVE) {
            /*check awards statuses*/
            for (award in rangedAwards) {
                if (award.statusDetails == Status.ACTIVE) throw ErrorException(ErrorType.ALREADY_HAVE_ACTIVE_AWARDS)
            }
            when (awardByBid.statusDetails) {
                Status.CONSIDERATION -> {
                    awardByBid.statusDetails = Status.ACTIVE
                    updateAward(awardByBid, dto.award, dateTime)
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    bidId = awardByBid.relatedBid
                    statusDetails = awardByBid.statusDetails.value()
                    lotAwarded = true
                    lotId = awardByBid.relatedLots[0]
                }
                Status.UNSUCCESSFUL -> {
                    awardByBid.statusDetails = Status.ACTIVE
                    updateAward(awardByBid, dto.award, dateTime)
                    bidId = awardByBid.relatedBid
                    statusDetails = awardByBid.statusDetails.value()
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    /*find next in status CONSIDERATION*/
                    nextAwardForUpdate = rangedAwards.asSequence().firstOrNull { it.statusDetails == Status.CONSIDERATION }
                    nextAwardForUpdate?.let {
                        it.statusDetails = Status.EMPTY
                        it.date = dateTime
                        lotAwarded = true
                        lotId = it.relatedLots[0]
                        saveAward(it, awardIdToEntityMap[it.id])
                    }
                }
                else -> throw ErrorException(ErrorType.INVALID_STATUS_DETAILS_SAVED_AWARD)
            }

            /*********************UNSUCCESSFUL*********************/
        } else if (dto.award.statusDetails == Status.UNSUCCESSFUL) {
            when (awardByBid.statusDetails) {
                Status.CONSIDERATION -> {
                    awardByBid.statusDetails = Status.UNSUCCESSFUL
                    updateAward(awardByBid, dto.award, dateTime)
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    bidId = awardByBid.relatedBid
                    statusDetails = awardByBid.statusDetails.value()
                    /*find next in status EMPTY*/
                    nextAwardForUpdate = rangedAwards.asSequence().firstOrNull { it.statusDetails == Status.EMPTY }
                    if (nextAwardForUpdate != null) {
                        nextAwardForUpdate.statusDetails = Status.CONSIDERATION
                        nextAwardForUpdate.date = dateTime
                        saveAward(nextAwardForUpdate, awardIdToEntityMap[nextAwardForUpdate.id])
                    } else {
                        lotAwarded = true
                        lotId = awardByBid.relatedLots[0]
                    }
                }
                Status.ACTIVE -> {
                    awardByBid.statusDetails = Status.UNSUCCESSFUL
                    updateAward(awardByBid, dto.award, dateTime)
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    bidId = awardByBid.relatedBid
                    statusDetails = awardByBid.statusDetails.value()
                    /*find next in status EMPTY*/
                    nextAwardForUpdate = rangedAwards.asSequence().firstOrNull { it.statusDetails == Status.EMPTY }
                    if (nextAwardForUpdate != null) {
                        nextAwardForUpdate.statusDetails = Status.CONSIDERATION
                        nextAwardForUpdate.date = dateTime
                        saveAward(nextAwardForUpdate, awardIdToEntityMap[nextAwardForUpdate.id])
                        lotAwarded = false
                        lotId = awardByBid.relatedLots[0]
                    }
                }
                else -> throw ErrorException(ErrorType.INVALID_STATUS_DETAILS_SAVED_AWARD)
            }
        }

        return ResponseDto(data = AwardByBidResponseDto(
                award = awardByBid,
                nextAwardForUpdate = nextAwardForUpdate,
                awardStatusDetails = statusDetails,
                bidId = bidId,
                lotId = lotId,
                lotAwarded = lotAwarded)
        )
    }

    private fun saveAward(award: Award, awardEntity: AwardEntity?) {
        if (awardEntity != null) {
            val newEntity = awardEntity.copy(
                    status = award.status!!.value(),
                    statusDetails = award.statusDetails.value(),
                    jsonData = toJson(award))
            awardDao.save(newEntity)
        }
    }

    private fun updateAward(awardFromBase: Award, awardFromRq: AwardByBid, dateTime: LocalDateTime) {
        awardFromBase.apply {
            description = awardFromRq.description
            documents = awardFromRq.documents
            date = dateTime
        }
    }

    private fun sortAwardsByCriteria(awards: Set<Award>, awardCriteria: AwardCriteria): List<Award> {
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
        return listOf()
    }

    private fun validation(award: Award, awardId: String, dto: AwardByBidRequestDto) {
        if (award.id != awardId) throw ErrorException(ErrorType.INVALID_ID)
        verifyDocumentsRelatedLots(award.relatedLots, dto.award.documents)
        verifyRequestStatusDetails(dto.award.statusDetails)
        verifyAwardByBidDocType(dto.award.documents)
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

    private fun verifyRequestStatusDetails(statusDetails: Status) {
        if (!(statusDetails == Status.ACTIVE || statusDetails == Status.UNSUCCESSFUL)) throw ErrorException(ErrorType.INVALID_STATUS_DETAILS)
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
}
