package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid

data class SetAwardForEvaluationContext(
    val cpid: Cpid,
    val ocid: Ocid
)
