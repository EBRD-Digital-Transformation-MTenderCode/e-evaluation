package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.Phase
import java.time.LocalDateTime

data class AwardCancellationContext(
    val cpid: String,
    val stage: String,
    val owner: String,
    val phase: Phase,
    val startDate: LocalDateTime
)
