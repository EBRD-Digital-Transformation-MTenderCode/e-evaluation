package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.award.AwardId

data class GetNextAwardContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val stage: String,
    val awardId: AwardId
)
