package com.procurement.evaluation.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class StartAwardPeriodResult(
    @JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @JsonProperty("awardPeriod") val awardPeriod: AwardPeriod
    ) {
        data class AwardPeriod(
            @JsonProperty("startDate") val startDate: LocalDateTime
        )
    }
}
