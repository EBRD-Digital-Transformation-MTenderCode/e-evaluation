package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.exception.ErrorType.CONTEXT
import com.procurement.evaluation.model.dto.BidsData
import com.procurement.evaluation.model.dto.CreateAwardsAuctionEndRq
import com.procurement.evaluation.model.dto.CreateAwardsAuctionRq
import com.procurement.evaluation.model.dto.CreateAwardsRq
import com.procurement.evaluation.model.dto.CreateAwardsRs
import com.procurement.evaluation.model.dto.FirstBid
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardCriteria
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.Bid
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.containsAny
import com.procurement.evaluation.utils.localNowUTC
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toLocal
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreateAwardService(
    private val rulesService: RulesService,
    private val periodService: PeriodService,
    private val awardDao: AwardDao,
    private val generationService: GenerationService
) {

    fun createAwards(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val country = cm.context.country ?: throw ErrorException(CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(CONTEXT)
        val awardCriteria = cm.context.awardCriteria ?: throw ErrorException(CONTEXT)
        val startDate = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateAwardsRq::class.java, cm.data)

        val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
        val relatedLotsFromBids = getRelatedLotsIdFromBids(dto.bids)
        val lotsFromTenderSet = getLotsFromTender(dto.lots)
        val uniqueLotsMap = getUniqueLotsMap(relatedLotsFromBids)
        val successfulLotsSet = getSuccessfulLots(uniqueLotsMap, minNumberOfBids)
        val unsuccessfulLotsSet = getUnsuccessfulLots(uniqueLotsMap, minNumberOfBids)
        addUnsuccessfulLots(lotsFromTenderSet, successfulLotsSet, unsuccessfulLotsSet)
        val successfulBidsList = getSuccessfulBids(dto.bids, successfulLotsSet)
        val successfulAwardsList = getSuccessfulAwards(successfulBidsList)
        sortSuccessfulAwards(successfulAwardsList, AwardCriteria.fromValue(awardCriteria))
        val unsuccessfulAwardsList = getUnsuccessfulAwards(unsuccessfulLotsSet)
        val awards = successfulAwardsList + unsuccessfulAwardsList

        val awardPeriod = if (successfulAwardsList.isEmpty()) {
            periodService.savePeriod(cpId, stage, startDate, startDate, awardCriteria)
        } else {
            periodService.saveStartOfPeriod(cpId, stage, startDate, awardCriteria)
        }
        awardDao.saveAll(getAwardEntities(awards, cpId, owner, stage))
        val unsuccessfulLots = getLotsDto(unsuccessfulLotsSet)
        val firstBids = getFirstBidsFromAwards(AwardCriteria.fromValue(awardCriteria), successfulAwardsList)
        return ResponseDto(data = CreateAwardsRs(awardPeriod, awards, unsuccessfulLots, firstBids))
    }

    fun createAwardsAuction(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val country = cm.context.country ?: throw ErrorException(CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(CONTEXT)
        val awardCriteria = cm.context.awardCriteria ?: throw ErrorException(CONTEXT)
        val startDate = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateAwardsAuctionRq::class.java, cm.data)

        val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
        val dtoBidsList = getBidsFromBidsData(dto.bidsData)
        val relatedLotsFromBids = getRelatedLotsIdFromBids(dtoBidsList)
        val lotsFromTenderSet = getLotsFromTender(dto.tender.lots)
        val uniqueLotsMap = getUniqueLotsMap(relatedLotsFromBids)
        val successfulLotsSet = getSuccessfulLots(uniqueLotsMap, minNumberOfBids)
        val unsuccessfulLotsSet = getUnsuccessfulLots(uniqueLotsMap, minNumberOfBids)
        addUnsuccessfulLots(lotsFromTenderSet, successfulLotsSet, unsuccessfulLotsSet)
        val successfulBidsList = getSuccessfulBids(dtoBidsList, successfulLotsSet)
        val successfulAwardsList = getSuccessfulAwards(successfulBidsList)
        sortSuccessfulAwards(successfulAwardsList, AwardCriteria.fromValue(awardCriteria))
        val unsuccessfulAwardsList = getUnsuccessfulAwards(unsuccessfulLotsSet)
        val awards = successfulAwardsList + unsuccessfulAwardsList
        val awardPeriod = if (successfulAwardsList.isEmpty()) {
            periodService.savePeriod(cpId, stage, startDate, startDate, awardCriteria)
        } else {
            periodService.saveStartOfPeriod(cpId, stage, startDate, awardCriteria)
        }
        awardDao.saveAll(getAwardEntities(awards, cpId, owner, stage))
        val unsuccessfulLots = getLotsDto(unsuccessfulLotsSet)
        val firstBids = getFirstBidsFromAwards(AwardCriteria.fromValue(awardCriteria), successfulAwardsList)
        return ResponseDto(data = CreateAwardsRs(awardPeriod, awards, unsuccessfulLots, firstBids))
    }

    fun createAwardsAuctionEnd(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val awardCriteria = cm.context.awardCriteria ?: throw ErrorException(CONTEXT)
        val startDate = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateAwardsAuctionEndRq::class.java, cm.data)

        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)

        val unsuccessfulLotsSet = getUnsuccessfulLotsFromAwardEntities(awardEntities)
        val successfulLotsSet = getRelatedLotsIdFromBids(dto.bids).asSequence()
            .filter { !unsuccessfulLotsSet.contains(it) }.toHashSet()
        val successfulBidsList = getSuccessfulBids(dto.bids, successfulLotsSet)
        val auctionResultList = dto.tender.electronicAuctions.details.asSequence()
            .flatMap { it.electronicAuctionResult.asSequence() }.toList()
        for (bid in successfulBidsList) {
            for (res in auctionResultList) {
                if (bid.id == res.relatedBid) {
                    if (bid.value.amount < res.value.amount) {
                        throw ErrorException(ErrorType.INVALID_AUCTION_RESULT)
                    }
                    bid.value = res.value
                }
            }
        }
        val awards = getSuccessfulAwards(successfulBidsList)
        sortSuccessfulAwards(awards, AwardCriteria.fromValue(awardCriteria))
        val awardPeriod = periodService.saveStartOfPeriod(cpId, stage, startDate, awardCriteria)
        awardDao.saveAll(getAwardEntities(awards, cpId, owner, stage))
        val unsuccessfulLots = getLotsDto(unsuccessfulLotsSet)
        val firstBids = getFirstBidsFromAwards(AwardCriteria.fromValue(awardCriteria), awards)
        return ResponseDto(data = CreateAwardsRs(awardPeriod, awards, unsuccessfulLots, firstBids))
    }

    fun createAwardsByLotsAuction(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val country = cm.context.country ?: throw ErrorException(CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateAwardsAuctionRq::class.java, cm.data)

        val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
        val dtoBidsList = getBidsFromBidsData(dto.bidsData)
        val relatedLotsFromBids = getRelatedLotsIdFromBids(dtoBidsList)
        val lotsFromTenderSet = getLotsFromTender(dto.tender.lots)
        val uniqueLotsMap = getUniqueLotsMap(relatedLotsFromBids)
        val successfulLotsSet = getSuccessfulLots(uniqueLotsMap, minNumberOfBids)
        val unsuccessfulLotsSet = getUnsuccessfulLots(uniqueLotsMap, minNumberOfBids)
        addUnsuccessfulLots(lotsFromTenderSet, successfulLotsSet, unsuccessfulLotsSet)
        val unsuccessfulAwardsList = getUnsuccessfulAwards(unsuccessfulLotsSet)
        val unsuccessfulLots = getLotsDto(unsuccessfulLotsSet)
        periodService.saveAwardCriteria(cpId, stage, dto.tender.awardCriteria)
        awardDao.saveAll(getAwardEntities(unsuccessfulAwardsList, cpId, owner, stage))
        return ResponseDto(data = CreateAwardsRs(null, unsuccessfulAwardsList, unsuccessfulLots, null))
    }

    private fun getFirstBidsFromAwards(awardCriteria: AwardCriteria, awards: List<Award>): Set<FirstBid>? {
        return if (awardCriteria == AwardCriteria.PRICE_ONLY) {
            awards.asSequence()
                .filter { it.status == AwardStatus.PENDING && it.statusDetails == AwardStatusDetails.CONSIDERATION }
                .map { FirstBid(it.relatedBid!!) }.toSet()
        } else null
    }

    private fun getAwardEntities(awards: List<Award>, cpId: String, owner: String, stage: String): List<AwardEntity> {
        val entities = ArrayList<AwardEntity>()
        awards.asSequence()
            .forEach { award ->
                entities.add(
                    getEntity(
                        award = award,
                        cpId = cpId,
                        stage = stage,
                        owner = owner,
                        token = UUID.fromString(award.token)
                    )
                )
            }
        return entities
    }

    private fun getUnsuccessfulLotsFromAwardEntities(awardEntities: List<AwardEntity>): HashSet<String> {
        val awards = awardEntities.asSequence().map { toObject(Award::class.java, it.jsonData) }.toSet()
        return awards.asSequence().flatMap { it.relatedLots.asSequence() }.toHashSet()
    }

    private fun getBidsFromBidsData(bidsData: Set<BidsData>): List<Bid> {
        return bidsData.asSequence()
            .flatMap { it.bids.asSequence() }
            .toList()
    }

    private fun getRelatedLotsIdFromBids(bids: List<Bid>): List<String> {
        return bids.asSequence().flatMap { it.relatedLots.asSequence() }.toList()
    }

    private fun getLotsFromTender(lots: List<Lot>): HashSet<String> {
        return lots.asSequence().map { it.id }.toHashSet()
    }

    private fun getUniqueLotsMap(lots: List<String>): Map<String, Int> {
        return lots.asSequence().groupBy { it }.mapValues { it.value.size }
    }

    private fun getSuccessfulLots(uniqueLots: Map<String, Int>, minNumberOfBids: Int): HashSet<String> {
        return uniqueLots.asSequence().filter { it.value >= minNumberOfBids }.map { it.key }.toHashSet()
    }

    private fun getUnsuccessfulLots(uniqueLots: Map<String, Int>, minNumberOfBids: Int): HashSet<String> {
        return uniqueLots.asSequence()
            .filter { it.value < minNumberOfBids }
            .map { it.key }
            .toHashSet()
    }

    private fun addUnsuccessfulLots(
        lotsFromTender: HashSet<String>,
        successfulLots: HashSet<String>,
        unsuccessfulLots: HashSet<String>
    ) {
        lotsFromTender.asSequence()
            .filter { !successfulLots.contains(it) && !unsuccessfulLots.contains(it) }
            .toCollection(unsuccessfulLots)
    }

    private fun getLotsDto(lots: HashSet<String>): List<Lot> {
        return lots.asSequence().map { Lot(it) }.toList()
    }

    private fun getSuccessfulBids(bids: List<Bid>, successfulLots: HashSet<String>): List<Bid> {
        return bids.asSequence().filter { successfulLots.containsAny(it.relatedLots) }.toList()
    }

    private fun getSuccessfulAwards(successfulBids: List<Bid>): List<Award> {
        return successfulBids.asSequence().map { bid ->
            Award(
                token = generationService.generateRandomUUID().toString(),
                id = generationService.getTimeBasedUUID(),
                date = localNowUTC(),
                description = null,
                title = null,
                status = AwardStatus.PENDING,
                statusDetails = AwardStatusDetails.EMPTY,
                value = bid.value,
                relatedLots = bid.relatedLots,
                relatedBid = bid.id,
                bidDate = bid.date,
                suppliers = bid.tenderers,
                documents = null,
                items = null,
                weightedValue = null
            )
        }.toList()
    }

    private fun getUnsuccessfulAwards(unSuccessfulLots: HashSet<String>): List<Award> {
        return unSuccessfulLots.asSequence().map { lot ->
            Award(
                token = generationService.generateRandomUUID().toString(),
                id = generationService.getTimeBasedUUID(),
                date = localNowUTC(),
                description = "Other reasons (discontinuation of procedure)",
                title = "The contract/lot is not awarded",
                status = AwardStatus.UNSUCCESSFUL,
                statusDetails = AwardStatusDetails.EMPTY,
                value = null,
                relatedLots = listOf(lot),
                relatedBid = null,
                bidDate = null,
                suppliers = null,
                documents = null,
                items = null,
                weightedValue = null
            )
        }.toList()
    }

    private fun sortSuccessfulAwards(awards: List<Award>, awardCriteria: AwardCriteria) {
        val lotIds = awards.asSequence().flatMap { it.relatedLots.asSequence() }.toSet()
        lotIds.forEach { lotId ->
            awards.asSequence()
                .filter { it.relatedLots.contains(lotId) }
                .sortedWith(compareBy<Award> { it.value?.amount }.thenBy { it.bidDate })
                .firstOrNull()
                ?.let { award -> award.statusDetails = AwardStatusDetails.CONSIDERATION }
        }
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

    private fun getEntity(
        award: Award,
        cpId: String,
        stage: String,
        owner: String,
        token: UUID
    ): AwardEntity {
        return AwardEntity(
            cpId = cpId,
            stage = stage,
            token = token,
            status = award.status.value,
            statusDetails = award.statusDetails.value,
            owner = owner,
            jsonData = toJson(award)
        )
    }
}
