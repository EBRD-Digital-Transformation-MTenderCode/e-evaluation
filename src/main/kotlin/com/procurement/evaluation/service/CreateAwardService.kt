package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType.*
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.ocds.*
import com.procurement.evaluation.model.dto.selections.CreateAwardsRq
import com.procurement.evaluation.model.dto.selections.CreateAwardsRs
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.*
import org.springframework.stereotype.Service
import java.util.*

interface CreateAwardService {

    fun createAwards(cm: CommandMessage): ResponseDto
}

@Service
class CreateAwardServiceImpl(private val rulesService: RulesService,
                             private val periodService: PeriodService,
                             private val awardDao: AwardDao,
                             private val generationService: GenerationService) : CreateAwardService {

    override fun createAwards(cm: CommandMessage): ResponseDto {
        val cpId = cm.context.cpid ?: throw ErrorException(CONTEXT)
        val stage = cm.context.stage ?: throw ErrorException(CONTEXT)
        val owner = cm.context.owner ?: throw ErrorException(CONTEXT)
        val country = cm.context.country ?: throw ErrorException(CONTEXT)
        val pmd = cm.context.pmd ?: throw ErrorException(CONTEXT)
        val startDate = cm.context.startDate?.toLocal() ?: throw ErrorException(CONTEXT)
        val dto = toObject(CreateAwardsRq::class.java, cm.data)

        val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
        val relatedLotsFromBids = getRelatedLotsIdFromBids(dto.bids)
        val lotsFromTenderSet = getLotsFromTender(dto.lots)
        val uniqueLotsMap = getUniqueLotsMap(relatedLotsFromBids)
        val successfulLotsSet = getSuccessfulLots(uniqueLotsMap, minNumberOfBids)
        val unsuccessfulLotsSet = getUnsuccessfulLots(uniqueLotsMap, minNumberOfBids)
        addUnsuccessfulLotsFromTender(lotsFromTenderSet, successfulLotsSet, unsuccessfulLotsSet)
        val successfulBidsList = getSuccessfulBids(dto.bids, successfulLotsSet)
        val successfulAwardsList = getSuccessfulAwards(successfulBidsList)
        sortSuccessfulAwards(successfulAwardsList, AwardCriteria.fromValue(dto.awardCriteria))
        val unsuccessfulAwardsList = getUnsuccessfulAwards(unsuccessfulLotsSet)
        val awards = successfulAwardsList + unsuccessfulAwardsList

        val awardPeriod = if (successfulAwardsList.isEmpty()) {
            periodService.savePeriod(cpId, stage, startDate, startDate, dto.awardCriteria)
        } else {
            periodService.saveStartOfPeriod(cpId, stage, startDate, dto.awardCriteria)
        }
        saveAwards(awards, cpId, owner, stage)
        val unsuccessfulLots = getLotsDto(unsuccessfulLotsSet)
        return ResponseDto(data = CreateAwardsRs(awardPeriod, awards, unsuccessfulLots))
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

    private fun addUnsuccessfulLotsFromTender(lotsFromTender: HashSet<String>,
                                              successfulLots: HashSet<String>,
                                              unsuccessfulLots: HashSet<String>) {
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
                    description = "",
                    status = Status.PENDING,
                    statusDetails = Status.EMPTY,
                    value = bid.value,
                    relatedLots = bid.relatedLots,
                    relatedBid = bid.id,
                    suppliers = bid.tenderers,
                    documents = null,
                    items = null)
        }.toList()
    }

    private fun getUnsuccessfulAwards(unSuccessfulLots: HashSet<String>): List<Award> {
        return unSuccessfulLots.asSequence().map { lot ->
            Award(
                    token = generationService.generateRandomUUID().toString(),
                    id = generationService.getTimeBasedUUID(),
                    date = localNowUTC(),
                    description = "",
                    status = Status.UNSUCCESSFUL,
                    statusDetails = Status.EMPTY,
                    value = null,
                    relatedLots = listOf(lot),
                    relatedBid = null,
                    suppliers = null,
                    documents = null,
                    items = null)
        }.toList()
    }

    private fun sortSuccessfulAwards(awards: List<Award>, awardCriteria: AwardCriteria) {
        when (awardCriteria) {
            AwardCriteria.PRICE_ONLY -> {
                val lotIds = awards.asSequence().flatMap { it.relatedLots.asSequence() }.toSet()
                lotIds.forEach { lotId ->
                    awards.asSequence()
                            .filter { it.relatedLots.contains(lotId) }
                            .sortedWith(SortedByValue)
                            .firstOrNull()
                            ?.let { it.statusDetails = Status.CONSIDERATION }
                }
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
    }

    companion object SortedByValue : Comparator<Award> {
        override fun compare(a: Award, b: Award): Int {
            return a.value!!.amount.compareTo(b.value!!.amount)
        }
    }

    fun saveAwards(awards: List<Award>, ocId: String, owner: String, stage: String) {
        awards.forEach { award ->
            val entity = getEntity(award = award, cpId = ocId, owner = owner, stage = stage)
            awardDao.save(entity)
        }
    }

    private fun getEntity(award: Award,
                          cpId: String,
                          owner: String,
                          stage: String): AwardEntity {
        val token = UUID.fromString(award.token ?: throw ErrorException(TOKEN))
        val status = award.status ?: throw ErrorException(STATUS)
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
