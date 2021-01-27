package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.ProcurementMethod
import java.time.LocalDateTime

data class CreateAwardsAuctionEndContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val owner: Owner,
    val startDate: LocalDateTime,
    val pmd: ProcurementMethod
)
