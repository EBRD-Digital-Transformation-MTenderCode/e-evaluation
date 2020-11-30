package com.procurement.evaluation.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class StartAwardPeriodResult(
    @field:JsonProperty("tender") @param:JsonProperty("tender") val tender: Tender
) {
    data class Tender(
        @field:JsonProperty("awardPeriod") @param:JsonProperty("awardPeriod") val awardPeriod: AwardPeriod
    ) {
        data class AwardPeriod(
            @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
        )
    }
}
