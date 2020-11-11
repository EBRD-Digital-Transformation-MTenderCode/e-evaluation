package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId

data class CheckAwardStatusContext(
    val cpid: Cpid,
    val ocid: Ocid,
    val stage: String,
    val token: Token,
    val owner: Owner,
    val awardId: AwardId
)
