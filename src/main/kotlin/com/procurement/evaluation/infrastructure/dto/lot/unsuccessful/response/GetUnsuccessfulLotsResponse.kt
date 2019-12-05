package com.procurement.evaluation.infrastructure.dto.lot.unsuccessful.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.lot.LotId

data class GetUnsuccessfulLotsResponse(
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("unsuccessfulLots") @param:JsonProperty("unsuccessfulLots") val lots: List<Lot>
) {

    data class Lot(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: LotId
    )
}
