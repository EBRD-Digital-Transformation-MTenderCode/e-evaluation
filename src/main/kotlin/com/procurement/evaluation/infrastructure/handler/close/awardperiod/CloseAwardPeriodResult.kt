package com.procurement.evaluation.infrastructure.handler.close.awardperiod


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class CloseAwardPeriodResult(
    @JsonProperty("awardPeriod") val awardPeriod: AwardPeriod
) {
    data class AwardPeriod(
        @JsonProperty("endDate") val endDate: LocalDateTime
    )
}
