package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.ProcurementMethod

data class FinalAwardsStatusByLotsContext(
    val cpid: Cpid,
    val pmd: ProcurementMethod
)
