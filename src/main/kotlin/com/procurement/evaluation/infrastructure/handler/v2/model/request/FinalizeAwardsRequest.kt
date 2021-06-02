package com.procurement.evaluation.infrastructure.handler.v2.model.request


import com.fasterxml.jackson.annotation.JsonProperty

data class FinalizeAwardsRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("contracts") @param:JsonProperty("contracts") val contracts: List<Contract>
) {
    data class Contract(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("awardId") @param:JsonProperty("awardId") val awardId: String
    )
}