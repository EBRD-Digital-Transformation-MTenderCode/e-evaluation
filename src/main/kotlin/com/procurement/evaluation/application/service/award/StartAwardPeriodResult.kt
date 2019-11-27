package com.procurement.evaluation.application.service.award

import java.time.LocalDateTime

data class StartAwardPeriodResult(
    val awardPeriod: AwardPeriod
) {
    data class AwardPeriod(
        val startDate: LocalDateTime
    )
}
