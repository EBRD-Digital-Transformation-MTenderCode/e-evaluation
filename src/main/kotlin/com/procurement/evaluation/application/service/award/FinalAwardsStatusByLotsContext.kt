package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.ProcurementMethod

data class FinalAwardsStatusByLotsContext(
    val cpid: String,
    val pmd: ProcurementMethod
)
