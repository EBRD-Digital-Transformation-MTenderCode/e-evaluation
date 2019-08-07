package com.procurement.evaluation.application.service.award

import java.util.*

data class GetEvaluatedAwardsContext(
    val cpid: String,
    val stage: String,
    val lotId: UUID
)
