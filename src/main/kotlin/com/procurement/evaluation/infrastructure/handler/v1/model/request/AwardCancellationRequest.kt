package com.procurement.evaluation.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class AwardCancellationRequest(
    @field:JsonProperty("lots") @param:JsonProperty("lots") val lots: List<Lot>
) {
    data class Lot(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
    )
}
