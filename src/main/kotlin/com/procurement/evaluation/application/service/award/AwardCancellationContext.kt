package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.model.dto.ocds.Phase
import java.time.LocalDateTime

data class AwardCancellationContext(
    val phase: Phase,
    val cpid: Cpid,
    val ocid: Ocid,
    val owner: String,
    val startDate: LocalDateTime
)
