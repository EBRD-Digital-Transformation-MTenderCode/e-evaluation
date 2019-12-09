package com.procurement.evaluation.infrastructure.dto.award.next

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails

data class GetNextAwardResponse(
    @field:JsonProperty("award") @param:JsonProperty("award") val award: Award
) {

    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: BidId
    )
}
