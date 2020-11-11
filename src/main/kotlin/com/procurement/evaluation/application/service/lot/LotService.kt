package com.procurement.evaluation.application.service.lot

import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.service.RulesService
import org.springframework.stereotype.Service

interface LotService {
    fun getUnsuccessfulLots(
        context: GetUnsuccessfulLotsContext,
        data: GetUnsuccessfulLotsData
    ): GetUnsuccessfulLotsResult
}

@Service
class LotServiceImpl(
    private val rulesService: RulesService
) : LotService {

    override fun getUnsuccessfulLots(
        context: GetUnsuccessfulLotsContext,
        data: GetUnsuccessfulLotsData
    ): GetUnsuccessfulLotsResult {
        val minNumberOfBids = rulesService.getRulesMinBids(country = context.country, method = context.pmd)

        val bidsIdsByLotId: Map<LotId, Set<BidId>> = mutableMapOf<LotId, MutableSet<BidId>>()
            .apply {
                data.bids.forEach { bid ->
                    addBidIdByLotId(lotIds = bid.relatedLots, bidId = bid.id)
                }
            }

        return GetUnsuccessfulLotsResult(
            lots = data.lots
                .filterUnsuccessfulLots(minNumberOfBids, bidsIdsByLotId)
                .map { lot ->
                    GetUnsuccessfulLotsResult.Lot(id = lot.id)
                }
                .toList()
        )
    }

    private fun MutableMap<LotId, MutableSet<BidId>>.addBidIdByLotId(lotIds: Collection<LotId>, bidId: BidId) {
        lotIds.forEach { lotId ->
            this.computeIfAbsent(lotId) { mutableSetOf() }
                .apply { add(bidId) }
        }
    }

    private fun List<GetUnsuccessfulLotsData.Lot>.filterUnsuccessfulLots(
        minNumberOfBids: Int,
        bidsIdsByLotId: Map<LotId, Set<BidId>>
    ): Sequence<GetUnsuccessfulLotsData.Lot> =
        this.asSequence()
            .filter { lot ->
                val bids = bidsIdsByLotId[lot.id]
                bids == null || bids.size < minNumberOfBids
            }
}
