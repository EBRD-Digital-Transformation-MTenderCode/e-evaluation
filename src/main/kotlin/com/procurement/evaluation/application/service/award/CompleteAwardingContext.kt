package com.procurement.evaluation.application.service.award

import java.time.LocalDateTime

data class CompleteAwardingContext(
    val cpid: String,
    val startDate: LocalDateTime
)
