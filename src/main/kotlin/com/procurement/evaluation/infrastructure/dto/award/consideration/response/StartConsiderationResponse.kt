package com.procurement.evaluation.infrastructure.dto.award.consideration.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails

data class StartConsiderationResponse(
    @field:JsonProperty("award") @param:JsonProperty("award") val award: Award
) {

    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>
    )
}
