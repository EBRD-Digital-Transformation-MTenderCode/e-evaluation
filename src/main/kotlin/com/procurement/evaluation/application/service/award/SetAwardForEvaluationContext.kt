package com.procurement.evaluation.application.service.award

data class SetAwardForEvaluationContext(
    val cpid: String,
    val ocid: String,
    val stage: String
)
