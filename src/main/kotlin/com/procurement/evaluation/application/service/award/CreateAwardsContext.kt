package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import java.time.LocalDateTime

data class CreateAwardsContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val owner: Owner,
    val startDate: LocalDateTime
)
