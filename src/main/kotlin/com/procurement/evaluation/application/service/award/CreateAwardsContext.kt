package com.procurement.evaluation.application.service.award

import java.time.LocalDateTime

data class CreateAwardsContext (
    val cpid: String,
    val ocid: String,
    val stage: String,
    val owner: String,
    val startDate: LocalDateTime
)