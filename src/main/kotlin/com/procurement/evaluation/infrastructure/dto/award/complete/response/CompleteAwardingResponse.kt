package com.procurement.evaluation.infrastructure.dto.award.complete.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class CompleteAwardingResponse(
    @field:JsonProperty("awardPeriod") @param:JsonProperty("awardPeriod") val awardPeriod: AwardPeriod
) {
    data class AwardPeriod(
        @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: LocalDateTime
    )
}
