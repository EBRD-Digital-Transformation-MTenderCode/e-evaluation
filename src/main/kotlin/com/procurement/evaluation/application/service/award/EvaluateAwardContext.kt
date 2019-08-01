package com.procurement.evaluation.application.service.award

import java.time.LocalDateTime
import java.util.*

data class EvaluateAwardContext(
    val cpid: String,
    val stage: String,
    val token: UUID,
    val owner: String,
    val startDate: LocalDateTime,
    val awardId: UUID
)
