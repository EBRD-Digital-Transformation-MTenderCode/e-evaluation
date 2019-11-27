package com.procurement.evaluation.application.service.award

import java.time.LocalDateTime

data class StartAwardPeriodContext(
    val cpid: String,
    val stage: String,
    val startDate: LocalDateTime
)
