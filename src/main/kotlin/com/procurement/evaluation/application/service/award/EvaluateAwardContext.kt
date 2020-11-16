package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.Token
import java.time.LocalDateTime
import java.util.*

data class EvaluateAwardContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val owner: Owner,
    val startDate: LocalDateTime,
    val awardId: UUID
)
