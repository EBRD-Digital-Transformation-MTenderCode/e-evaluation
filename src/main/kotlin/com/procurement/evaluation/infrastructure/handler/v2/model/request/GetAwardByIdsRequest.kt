package com.procurement.evaluation.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class GetAwardByIdsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
    )
}
