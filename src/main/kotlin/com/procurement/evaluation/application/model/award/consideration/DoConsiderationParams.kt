package com.procurement.evaluation.application.model.award.consideration

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid

data class DoConsiderationParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val awards: List<Award>
) {
    data class Award(
        val id: String
    )
}