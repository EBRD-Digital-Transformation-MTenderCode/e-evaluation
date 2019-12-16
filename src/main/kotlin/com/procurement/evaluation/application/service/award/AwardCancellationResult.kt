package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime

data class AwardCancellationResult(
    val awards: List<Award>
) {
    data class Award(
        val id: AwardId,
        val title: String?,
        val description: String?,
        val date: LocalDateTime?,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<LotId>?
    )
}
