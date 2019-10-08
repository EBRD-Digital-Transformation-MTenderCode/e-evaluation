package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.util.*

data class FinalizedAwardsStatusByLots(
    val awards: List<Award>
) {

    data class Award(
        val id: UUID,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails
    )
}
