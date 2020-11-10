package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import java.time.LocalDateTime

data class StartAwardPeriodContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val startDate: LocalDateTime
)
