package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import java.util.*

data class GetEvaluatedAwardsContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val stage: String,
    val lotId: UUID
)
