package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import java.time.LocalDateTime
import java.util.*

data class EvaluateAwardContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val stage: String,
    val token: UUID,
    val owner: String,
    val startDate: LocalDateTime,
    val awardId: UUID
)
