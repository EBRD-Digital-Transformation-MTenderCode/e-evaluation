package com.procurement.evaluation.infrastructure.dto.award.period.start


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class StartAwardPeriodResponse(
    @field:JsonProperty("awardPeriod") @param:JsonProperty("awardPeriod") val awardPeriod: AwardPeriod
) {
    data class AwardPeriod(
        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
    )
}