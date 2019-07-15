package com.procurement.evaluation.application.service.award

import java.time.LocalDateTime
import java.util.*

data class CreateAwardContext(
    val cpid: String,
    val stage: String,
    val owner: String,
    val startDate: LocalDateTime,
    val lotId: UUID
)
