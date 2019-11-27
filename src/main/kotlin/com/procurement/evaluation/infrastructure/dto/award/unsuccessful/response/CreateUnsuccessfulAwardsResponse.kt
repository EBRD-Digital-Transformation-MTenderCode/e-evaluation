package com.procurement.evaluation.infrastructure.dto.award.unsuccessful.response


import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime
import java.util.*

data class CreateUnsuccessfulAwardsResponse(
    @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
        @field:JsonProperty("status") @param:JsonProperty("status") val status: AwardStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<UUID>
    )
}