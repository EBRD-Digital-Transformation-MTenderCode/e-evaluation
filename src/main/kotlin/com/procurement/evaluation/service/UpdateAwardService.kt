package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType.ALREADY_HAVE_ACTIVE_AWARDS
import com.procurement.evaluation.exception.ErrorType.CONTEXT
import com.procurement.evaluation.exception.ErrorType.DATA_NOT_FOUND
import com.procurement.evaluation.exception.ErrorType.ID
import com.procurement.evaluation.exception.ErrorType.INVALID_OWNER
import com.procurement.evaluation.exception.ErrorType.INVALID_STATUS_DETAILS
import com.procurement.evaluation.exception.ErrorType.RELATED_LOTS
import com.procurement.evaluation.exception.ErrorType.STATUS_DETAILS_SAVED_AWARD
import com.procurement.evaluation.model.dto.AwardByBid
import com.procurement.evaluation.model.dto.AwardByBidRq
import com.procurement.evaluation.model.dto.AwardByBidRs
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardCriteria
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.Document
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toLocal
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class UpdateAwardService(private val awardDao: AwardDao) {

    fun awardByBid(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val token = cm.context.token ?: throw ErrorException(CONTEXT)
        val awardId = cm.context.id ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val awardCriteria = AwardCriteria.fromValue(cm.context.awardCriteria ?: throw ErrorException(CONTEXT))
        val dateTime = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val dto = toObject(AwardByBidRq::class.java, cm.data)

        val awardEntity = awardDao.getByCpIdAndStageAndToken(cpId, stage, UUID.fromString(token))
        if (awardEntity.owner != owner) throw ErrorException(INVALID_OWNER)
        val awardByBid = toObject(Award::class.java, awardEntity.jsonData)
        validation(awardByBid, awardId, dto)
        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        if (awardEntities.isEmpty()) throw ErrorException(DATA_NOT_FOUND)
        val awardIdToEntityMap: MutableMap<String, AwardEntity> = mutableMapOf()
        val awardFromEntitiesSet: MutableSet<Award> = mutableSetOf()
        awardEntities.forEach { entity ->
            val award = toObject(Award::class.java, entity.jsonData)
            if (award.relatedLots == awardByBid.relatedLots) {
                awardIdToEntityMap[award.id] = entity
                awardFromEntitiesSet.add(award)
            }
        }

        val rangedAwards = sortAwardsByCriteria(awardFromEntitiesSet, awardCriteria)

        var bidId: String? = null
        var consideredBidId: String? = null
        var statusDetails: String? = null
        var lotId: String? = null
        var lotAwarded: Boolean? = null
        var bidAwarded = true
        var nextAwardForUpdate: Award? = null

        /*********************ACTIVE*********************/
        if (dto.award.statusDetails == AwardStatusDetails.ACTIVE) {
            /*check awards statuses*/
            for (award in rangedAwards) {
                if (award.id != awardId && award.statusDetails == AwardStatusDetails.ACTIVE)
                    throw ErrorException(ALREADY_HAVE_ACTIVE_AWARDS)
            }
            when (awardByBid.statusDetails) {
                AwardStatusDetails.ACTIVE -> {
                    updateAward(awardByBid, dto.award, dateTime)
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    bidAwarded = false
                }
                AwardStatusDetails.CONSIDERATION -> {
                    awardByBid.statusDetails = AwardStatusDetails.ACTIVE
                    updateAward(awardByBid, dto.award, dateTime)
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    bidId = awardByBid.relatedBid
                    statusDetails = awardByBid.statusDetails.value
                    lotAwarded = true
                    lotId = awardByBid.relatedLots[0]
                }
                AwardStatusDetails.UNSUCCESSFUL -> {
                    awardByBid.statusDetails = AwardStatusDetails.ACTIVE
                    updateAward(awardByBid, dto.award, dateTime)
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    bidId = awardByBid.relatedBid
                    statusDetails = awardByBid.statusDetails.value
                    /*find next in status CONSIDERATION*/
                    nextAwardForUpdate = rangedAwards.asSequence().firstOrNull { it.statusDetails == AwardStatusDetails.CONSIDERATION }
                    if (nextAwardForUpdate != null) {
                        nextAwardForUpdate.statusDetails = AwardStatusDetails.EMPTY
                        nextAwardForUpdate.date = dateTime
                        saveAward(nextAwardForUpdate, awardIdToEntityMap[nextAwardForUpdate.id])
                        lotAwarded = true
                        lotId = nextAwardForUpdate.relatedLots[0]
                    }
                }
                else -> throw ErrorException(STATUS_DETAILS_SAVED_AWARD)
            }

            /*********************UNSUCCESSFUL*********************/
        } else if (dto.award.statusDetails == AwardStatusDetails.UNSUCCESSFUL) {
            when (awardByBid.statusDetails) {
                AwardStatusDetails.UNSUCCESSFUL -> {
                    updateAward(awardByBid, dto.award, dateTime)
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    bidAwarded = false
                }
                AwardStatusDetails.CONSIDERATION -> {
                    awardByBid.statusDetails = AwardStatusDetails.UNSUCCESSFUL
                    updateAward(awardByBid, dto.award, dateTime)
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    bidId = awardByBid.relatedBid
                    statusDetails = awardByBid.statusDetails.value
                    /*find next in status EMPTY*/
                    nextAwardForUpdate = rangedAwards.asSequence().firstOrNull { it.statusDetails == AwardStatusDetails.EMPTY }
                    if (nextAwardForUpdate != null) {
                        nextAwardForUpdate.statusDetails = AwardStatusDetails.CONSIDERATION
                        nextAwardForUpdate.date = dateTime
                        if (awardCriteria == AwardCriteria.PRICE_ONLY) {
                            consideredBidId = nextAwardForUpdate.relatedBid
                        }
                        saveAward(nextAwardForUpdate, awardIdToEntityMap[nextAwardForUpdate.id])
                    } else {
                        lotAwarded = true
                        lotId = awardByBid.relatedLots[0]
                    }
                }
                AwardStatusDetails.ACTIVE -> {
                    awardByBid.statusDetails = AwardStatusDetails.UNSUCCESSFUL
                    updateAward(awardByBid, dto.award, dateTime)
                    saveAward(awardByBid, awardIdToEntityMap[awardByBid.id])
                    bidId = awardByBid.relatedBid
                    statusDetails = awardByBid.statusDetails.value
                    /*find next in status EMPTY*/
                    nextAwardForUpdate = rangedAwards.asSequence().firstOrNull { it.statusDetails == AwardStatusDetails.EMPTY }
                    if (nextAwardForUpdate != null) {
                        nextAwardForUpdate.statusDetails = AwardStatusDetails.CONSIDERATION
                        nextAwardForUpdate.date = dateTime
                        if (awardCriteria == AwardCriteria.PRICE_ONLY) {
                            consideredBidId = nextAwardForUpdate.relatedBid
                        }
                        saveAward(nextAwardForUpdate, awardIdToEntityMap[nextAwardForUpdate.id])
                        lotAwarded = false
                        lotId = awardByBid.relatedLots[0]
                    }
                }
                else -> throw ErrorException(STATUS_DETAILS_SAVED_AWARD)
            }
        }

        return ResponseDto(data = AwardByBidRs(
                award = awardByBid,
                nextAwardForUpdate = nextAwardForUpdate,
                awardStatusDetails = statusDetails,
                bidId = bidId,
                consideredBidId = consideredBidId,
                lotId = lotId,
                lotAwarded = lotAwarded,
                bidAwarded = bidAwarded)
        )
    }

    private fun saveAward(award: Award, awardEntity: AwardEntity?) {
        if (awardEntity != null) {
            val newEntity = awardEntity.copy(
                    status = award.status.value,
                    statusDetails = award.statusDetails.value,
                    jsonData = toJson(award))
            awardDao.save(newEntity)
        }
    }

    private fun updateAward(awardFromBase: Award, awardDto: AwardByBid, dateTime: LocalDateTime) {
        awardFromBase.apply {
            description = awardDto.description
            documents = updateDocuments(awardFromBase.documents, awardDto.documents)
            date = dateTime
        }
    }

    private fun updateDocuments(documentsDb: List<Document>?, documentsDto: List<Document>?): List<Document>? {
        return if (documentsDb != null && documentsDb.isNotEmpty()) {
            if (documentsDto != null) {
                val documentsDtoId = documentsDto.asSequence().map { it.id }.toSet()
                val documentsDbId = documentsDb.asSequence().map { it.id }.toSet()
                val newDocumentsId = documentsDtoId - documentsDbId
                //update
                documentsDb.forEach { document ->
                    documentsDto.firstOrNull { it.id == document.id }
                            ?.let { document.updateDocument(it) }
                }
                //new
                val newDocuments = documentsDto.asSequence()
                        .filter { it.id in newDocumentsId }.toList()
                documentsDb + newDocuments
            } else {
                documentsDb
            }

        } else {
            documentsDto
        }
    }

    private fun Document.updateDocument(documentDto: Document) {
        this.title = documentDto.title
        this.description = documentDto.description
        this.relatedLots = documentDto.relatedLots
    }

    private fun sortAwardsByCriteria(awards: Set<Award>, awardCriteria: AwardCriteria): List<Award> {
        return awards.sortedWith(compareBy<Award> { it.value?.amount }.thenBy { it.bidDate })
//        when (awardCriteria) {
//            AwardCriteria.PRICE_ONLY -> {
//            }
//            AwardCriteria.COST_ONLY -> {
//            }
//            AwardCriteria.QUALITY_ONLY -> {
//            }
//            AwardCriteria.RATED_CRITERIA -> {
//            }
//        }
    }

    private fun validation(award: Award, awardId: String, dto: AwardByBidRq) {
        if (award.id != awardId) throw ErrorException(ID)
        verifyDocumentsRelatedLots(award.relatedLots, dto.award.documents)
        verifyRequestStatusDetails(dto.award.statusDetails)
    }

    private fun verifyDocumentsRelatedLots(relatedLots: List<String>, documents: List<Document>?) {
        if (documents != null) {
            for (document in documents) {
                val docRelatedLots = mutableListOf<String>()
                document.relatedLots?.toCollection(docRelatedLots)
                if (!relatedLots.containsAll(docRelatedLots)) throw ErrorException(RELATED_LOTS)
            }
        }
    }

    private fun verifyRequestStatusDetails(statusDetails: AwardStatusDetails) {
        if (!(statusDetails == AwardStatusDetails.ACTIVE || statusDetails == AwardStatusDetails.UNSUCCESSFUL))
            throw ErrorException(INVALID_STATUS_DETAILS)
    }

}