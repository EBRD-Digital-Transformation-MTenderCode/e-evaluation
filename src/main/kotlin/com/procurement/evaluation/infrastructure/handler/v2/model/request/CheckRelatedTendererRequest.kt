package com.procurement.evaluation.infrastructure.handler.v2.model.request


import com.fasterxml.jackson.annotation.JsonProperty

data class CheckRelatedTendererRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("awardId") @field:JsonProperty("awardId") val awardId: String,
    @param:JsonProperty("requirementId") @field:JsonProperty("requirementId") val requirementId: String,
    @param:JsonProperty("relatedTendererId") @field:JsonProperty("relatedTendererId") val relatedTendererId: String,
    @param:JsonProperty("responderId") @field:JsonProperty("responderId") val responderId: String
)