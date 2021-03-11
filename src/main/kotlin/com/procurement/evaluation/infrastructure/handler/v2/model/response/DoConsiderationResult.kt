package com.procurement.evaluation.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails

data class DoConsiderationResult(
    @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("status") @field:JsonProperty("status") val status: AwardStatus,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("relatedBid") @field:JsonProperty("relatedBid") val relatedBid: String?
    )
}