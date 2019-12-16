package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.Phase

data class AwardCancellationContext(
    val cpid: String,
    val stage: String,
    val owner: String,
    val phase: Phase
)
