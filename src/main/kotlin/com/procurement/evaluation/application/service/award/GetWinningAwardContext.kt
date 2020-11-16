package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import java.util.*

data class GetWinningAwardContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val lotId: UUID
)
