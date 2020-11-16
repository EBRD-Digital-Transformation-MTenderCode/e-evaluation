package com.procurement.evaluation.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonProperty

data class GetAwardStateByIdsRequest(
    @param:JsonProperty("awardIds") @field:JsonProperty("awardIds") val awardIds: List<String>,
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String
)
