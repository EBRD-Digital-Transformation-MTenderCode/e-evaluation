package com.procurement.evaluation.application.service.lot

import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.lot.RelatedLots

data class GetUnsuccessfulLotsData(
    val bids: List<Bid>,
    val lots: List<Lot>
) {

    data class Lot(
        val id: LotId
    )

    data class Bid(
        val id: BidId,
        override val relatedLots: List<LotId>
    ) : RelatedLots<LotId>
}
