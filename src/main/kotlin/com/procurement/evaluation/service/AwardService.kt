package com.procurement.evaluation.service

import com.procurement.evaluation.dao.AwardDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.model.dto.AwardsResponseDto
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.ocds.*
import com.procurement.evaluation.model.dto.selections.SelectionsRequestDto
import com.procurement.evaluation.model.dto.selections.SelectionsResponseDto
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.containsAny
import com.procurement.evaluation.utils.localNowUTC
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

interface AwardService {

    fun createAwards(cpId: String,
                     stage: String,
                     owner: String,
                     country: String,
                     pmd: String,
                     awardCriteria: String,
                     startDate: LocalDateTime,
                     dto: SelectionsRequestDto): ResponseDto

    fun getAwards(cpId: String,
                  stage: String,
                  country: String,
                  pmd: String): ResponseDto
}

@Service
class AwardServiceImpl(private val rulesService: RulesService,
                       private val periodService: PeriodService,
                       private val awardDao: AwardDao,
                       private val generationService: GenerationService) : AwardService {

    override fun createAwards(cpId: String,
                              stage: String,
                              owner: String,
                              country: String,
                              pmd: String,
                              awardCriteria: String,
                              startDate: LocalDateTime,
                              dto: SelectionsRequestDto): ResponseDto {

        val minNumberOfBids = rulesService.getRulesMinBids(country, pmd)
        val relatedLotsFromBids = getRelatedLotsIdFromBids(dto.bids)
        val lotsFromTenderSet = getLotsFromTender(dto.lots)
        val uniqueLotsMap = getUniqueLotsMap(relatedLotsFromBids)
        val successfulLotsSet = getSuccessfulLots(uniqueLotsMap, minNumberOfBids)
        val unsuccessfulLotsSet = getUnsuccessfulLots(uniqueLotsMap, minNumberOfBids)
        addUnsuccessfulLotsFromTender(lotsFromTenderSet, successfulLotsSet, unsuccessfulLotsSet)
        val successfulBidsList = getSuccessfulBids(dto.bids, successfulLotsSet)
        val successfulAwardsList = getSuccessfulAwards(successfulBidsList)
        sortSuccessfulAwards(successfulAwardsList, AwardCriteria.fromValue(awardCriteria))
        val unsuccessfulAwardsList = getUnsuccessfulAwards(unsuccessfulLotsSet)
        val awards = successfulAwardsList + unsuccessfulAwardsList

        val awardPeriod = if (successfulAwardsList.isEmpty()) {
            periodService.savePeriod(cpId, stage, startDate, startDate,awardCriteria)
        } else {
            periodService.saveStartOfPeriod(cpId, stage, startDate,awardCriteria)
        }
        saveAwards(awards, cpId, owner, stage)
        val lotsDtoList = getLotsDto(unsuccessfulLotsSet)
        return ResponseDto(true, null, SelectionsResponseDto(awardPeriod, awards, lotsDtoList))
    }

    override fun getAwards(cpId: String,
                           stage: String,
                           country: String,
                           pmd: String): ResponseDto {
        val awardEntities = awardDao.findAllByCpIdAndStage(cpId, stage)
        val activeAwards = getPendingAwardsFromEntities(awardEntities)
        return ResponseDto(true, null, AwardsResponseDto(activeAwards, null, null))
    }

    private fun getPendingAwardsFromEntities(awardEntities: List<AwardEntity>): List<Award> {
        return awardEntities.asSequence()
                .map { entity -> toObject(Award::class.java, entity.jsonData) }
                .filter { award -> award.status == Status.PENDING }.toList()
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
                    token = null,
                    id = generationService.generateTimeBasedUUID().toString(),
                    date = localNowUTC(),
                    description = "",
                    status = Status.PENDING,
                    statusDetails = Status.EMPTY,
                    value = bid.value,
                    relatedLots = bid.relatedLots,
                    relatedBid = bid.id,
                    suppliers = bid.tenderers,
                    documents = null)
        }.toList()
    }

    private fun getUnsuccessfulAwards(unSuccessfulLots: HashSet<String>): List<Award> {
        return unSuccessfulLots.asSequence().map { lot ->
            Award(
                    token = null,
                    id = generationService.generateTimeBasedUUID().toString(),
                    date = localNowUTC(),
                    description = "",
                    status = Status.UNSUCCESSFUL,
                    statusDetails = Status.EMPTY,
                    value = null,
                    relatedLots = listOf(lot),
                    relatedBid = null,
                    suppliers = null,
                    documents = null)
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
        awards.forEach { awardDto ->
            val entity = getEntity(awardDto, ocId, owner, stage)
            awardDao.save(entity)
            if (awardDto.status != Status.UNSUCCESSFUL) awardDto.token = entity.token.toString()
        }
    }

    private fun getEntity(award: Award,
                          cpId: String,
                          owner: String,
                          stage: String): AwardEntity {
        val status = award.status ?: throw ErrorException(ErrorType.INVALID_STATUS)
        return AwardEntity(
                cpId = cpId,
                stage = stage,
                token = generationService.generateRandomUUID(),
                status = status.value(),
                statusDetails = award.statusDetails.value(),
                owner = owner,
                jsonData = toJson(award))
    }

}
