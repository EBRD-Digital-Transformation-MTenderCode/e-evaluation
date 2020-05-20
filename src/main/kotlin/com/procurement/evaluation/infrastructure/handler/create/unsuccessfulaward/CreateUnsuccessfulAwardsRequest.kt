package com.procurement.evaluation.infrastructure.handler.create.unsuccessfulaward


import com.fasterxml.jackson.annotation.JsonProperty

data class CreateUnsuccessfulAwardsRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("lotIds") @param:JsonProperty("lotIds") val lotIds: List<String>,
    @field:JsonProperty("date") @param:JsonProperty("date") val date: String
)
