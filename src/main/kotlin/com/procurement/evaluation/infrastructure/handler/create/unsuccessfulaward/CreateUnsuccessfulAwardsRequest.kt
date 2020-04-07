package com.procurement.evaluation.infrastructure.handler.create.unsuccessfulaward


import com.fasterxml.jackson.annotation.JsonProperty

data class CreateUnsuccessfulAwardsRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("owner") @param:JsonProperty("owner") val owner: String,
    @field:JsonProperty("lotIds") @param:JsonProperty("lotIds") val lotIds: List<String>,
    @field:JsonProperty("requestDate") @param:JsonProperty("requestDate") val requestDate: String
)
