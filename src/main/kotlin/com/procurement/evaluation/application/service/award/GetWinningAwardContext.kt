package com.procurement.evaluation.application.service.award

import java.util.*

data class GetWinningAwardContext(
    val cpid: String,
    val stage: String,
    val lotId: UUID
)
