package com.procurement.evaluation.infrastructure.dto.award.create.auction.end.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId

data class CreateAwardsAuctionEndResponse(
    @field: JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId
    )
}
