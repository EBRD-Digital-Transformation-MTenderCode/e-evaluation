package com.procurement.evaluation.infrastructure.handler.v2.model.response


import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails

data class FinalizeAwardsResult(
    @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: AwardStatus,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
        @param:JsonProperty("relatedBid") @field:JsonProperty("relatedBid") val relatedBid: BidId
    )
}