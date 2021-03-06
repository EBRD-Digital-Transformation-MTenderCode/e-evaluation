package com.procurement.evaluation.infrastructure.handler.v2.model.response


import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime

data class CreateUnsuccessfulAwardsResult(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,
    @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
    @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
    @field:JsonProperty("status") @param:JsonProperty("status") val status: AwardStatus,
    @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
    @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>
)
