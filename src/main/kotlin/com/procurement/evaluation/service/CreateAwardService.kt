package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType.CONTEXT
import com.procurement.evaluation.model.dto.BidsData
import com.procurement.evaluation.model.dto.CreateAwardsAuctionRq
import com.procurement.evaluation.model.dto.CreateAwardsRs
import com.procurement.evaluation.model.dto.FirstBid
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.cpid
import com.procurement.evaluation.model.dto.bpe.ocid
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
    fun createAwardsAuction(cm: CommandMessage): CreateAwardsRs {
        val cpid = cm.cpid
        val ocid = cm.ocid

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
        sortSuccessfulAwards(successfulAwardsList)
        val unsuccessfulAwardsList = getUnsuccessfulAwards(unsuccessfulLotsSet)
        val awards = successfulAwardsList + unsuccessfulAwardsList
        val awardPeriod = if (successfulAwardsList.isEmpty()) {
            periodService.savePeriod(cpid, ocid, startDate, startDate, awardCriteria)
        } else {
            periodService.saveStartOfPeriod(cpid, ocid, startDate, awardCriteria)
        }
        awardDao.saveAll(getAwardEntities(awards, cpid, owner, stage))
        val unsuccessfulLots = getLotsDto(unsuccessfulLotsSet)
        val firstBids = getFirstBidsFromAwards(AwardCriteria.creator(awardCriteria), successfulAwardsList)
        return CreateAwardsRs(awardPeriod, awards, unsuccessfulLots, firstBids)
    }

    private fun getFirstBidsFromAwards(awardCriteria: AwardCriteria, awards: List<Award>): Set<FirstBid>? {
        return if (awardCriteria == AwardCriteria.PRICE_ONLY) {
            awards.asSequence()
                .filter { it.status == AwardStatus.PENDING && it.statusDetails == AwardStatusDetails.CONSIDERATION }
                .map { FirstBid(it.relatedBid!!) }.toSet()
        } else null
    }

    private fun getAwardEntities(awards: List<Award>, cpid: Cpid, owner: String, stage: String): List<AwardEntity> {
        val entities = ArrayList<AwardEntity>()
        awards.asSequence()
            .forEach { award ->
                entities.add(
                    getEntity(
                        award = award,
                        cpid = cpid,
                        stage = stage,
                        owner = owner,
                        token = UUID.fromString(award.token)
                    )
                )
            }
        return entities
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

    private fun sortSuccessfulAwards(awards: List<Award>) {
        val lotIds = awards.asSequence().flatMap { it.relatedLots.asSequence() }.toSet()
        lotIds.forEach { lotId ->
            awards.asSequence()
                .filter { it.relatedLots.contains(lotId) }
                .sortedWith(compareBy<Award> { it.value?.amount }.thenBy { it.bidDate })
                .firstOrNull()
                ?.let { award -> award.statusDetails = AwardStatusDetails.CONSIDERATION }
        }
    }

    private fun getEntity(
        award: Award,
        cpid: Cpid,
        stage: String,
        owner: String,
        token: UUID
    ): AwardEntity {
        return AwardEntity(
            cpId = cpid.toString(),
            stage = stage,
            token = token,
            status = award.status.key,
            statusDetails = award.statusDetails.key,
            owner = owner,
            jsonData = toJson(award)
        )
    }
}
