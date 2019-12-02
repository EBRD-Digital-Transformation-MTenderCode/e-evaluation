package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.enums.OperationType
import java.time.LocalDateTime

data class CreateUnsuccessfulAwardsContext(
    val cpid: String,
    val stage: String,
    val token: Token,
    val owner: String,
    val operationType: OperationType,
    val startDate: LocalDateTime
)