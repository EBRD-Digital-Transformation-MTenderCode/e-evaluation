package com.procurement.evaluation.application.model.award.finalize

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.award.AwardId

data class FinalizeAwardsParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val contracts: List<Contract>
) {
    data class Contract(
        val id: String,
        val awardId: AwardId
    )
}