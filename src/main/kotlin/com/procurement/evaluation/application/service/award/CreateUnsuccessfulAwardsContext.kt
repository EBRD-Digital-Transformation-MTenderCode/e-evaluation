package com.procurement.evaluation.application.service.award

import java.time.LocalDateTime
import java.util.*

data class CreateUnsuccessfulAwardsContext(
    val cpid: String,
    val stage: String,
    val token: UUID,
    val owner: String,
    val operationType: String,
    val startDate: LocalDateTime
)
