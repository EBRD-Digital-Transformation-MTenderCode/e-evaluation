package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.Phase
import java.time.LocalDateTime

data class AwardCancellationContext(
    val stage: String,
    val phase: Phase,
    val cpid: String,
    val owner: String,
    val startDate: LocalDateTime
)
