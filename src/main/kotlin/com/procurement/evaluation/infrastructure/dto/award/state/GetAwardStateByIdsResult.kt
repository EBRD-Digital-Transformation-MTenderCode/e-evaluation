package com.procurement.evaluation.infrastructure.dto.award.state


import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails

data class GetAwardStateByIdsResult(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId,
    @param:JsonProperty("status") @field:JsonProperty("status") val status: AwardStatus,
    @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails
)