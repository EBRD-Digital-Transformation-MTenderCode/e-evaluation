package com.procurement.evaluation.infrastructure.handler.v2.model.request


import com.fasterxml.jackson.annotation.JsonProperty

data class CloseAwardPeriodRequest(
    @field:JsonProperty("cpid") @param:JsonProperty("cpid") val cpid: String,
    @field:JsonProperty("ocid") @param:JsonProperty("ocid") val ocid: String,
    @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: String
)
