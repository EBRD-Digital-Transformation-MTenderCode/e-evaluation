package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import java.time.LocalDateTime
import java.util.*

data class CreateAwardContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val owner: Owner,
    val startDate: LocalDateTime,
    val lotId: UUID
)
