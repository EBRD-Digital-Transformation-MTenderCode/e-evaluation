package com.procurement.evaluation.infrastructure.dto.award.initial.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime

data class SetInitialAwardsStatusResponse(
    @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: AwardStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: BidId
    )
}
