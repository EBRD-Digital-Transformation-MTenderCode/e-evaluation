package com.procurement.evaluation.application.model.award.finalize

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.lot.LotId

data class FinalizeAwardsParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val tender: Tender
) {
    data class Tender(
        val lots: List<Lot>
    ) {
        data class Lot(
            val id: LotId
        )
    }
}