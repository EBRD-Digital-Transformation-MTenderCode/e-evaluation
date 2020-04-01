package com.procurement.evaluation.infrastructure.dto.award.state

import com.fasterxml.jackson.annotation.JsonProperty

data class GetAwardStateByIdsRequest(
    @param:JsonProperty("awardIds") @field:JsonProperty("awardIds") val awardIds: List<String>?,
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String
)