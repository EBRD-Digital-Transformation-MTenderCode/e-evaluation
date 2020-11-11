package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.enums.OperationType
import java.time.LocalDateTime

data class CreateUnsuccessfulAwardsContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val owner: String,
    val operationType: OperationType,
    val startDate: LocalDateTime
)
