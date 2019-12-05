package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails

data class StartConsiderationResult(
    val award: Award
) {

    data class Award(
        val id: AwardId,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<LotId>
    )
}
