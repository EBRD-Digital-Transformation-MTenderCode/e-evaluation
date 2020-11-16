package com.procurement.evaluation.infrastructure.handler.v2.model.response


import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class CloseAwardPeriodResult(
    @JsonProperty("awardPeriod") val awardPeriod: AwardPeriod
) {
    data class AwardPeriod(
        @JsonProperty("endDate") val endDate: LocalDateTime
    )
}
