package com.procurement.evaluation.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.lot.LotId

data class GetUnsuccessfulLotsRequest(
    @field:JsonProperty("bids") @param:JsonProperty("bids") val bids: List<Bid>,
    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
) {

    data class Lot(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
    )

    data class Bid(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: BidId,
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>
    )
}
