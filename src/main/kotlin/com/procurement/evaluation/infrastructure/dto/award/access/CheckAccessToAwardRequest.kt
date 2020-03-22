package com.procurement.evaluation.infrastructure.dto.award.access


import com.fasterxml.jackson.annotation.JsonProperty

data class CheckAccessToAwardRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("token") @field:JsonProperty("token") val token: String,
    @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String,
    @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: String
)