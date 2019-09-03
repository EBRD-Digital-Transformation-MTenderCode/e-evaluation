package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime
import java.util.*

data class AwardCancelledData(
    val awards: List<Award>
) {
    data class Award(
        val id: UUID,
        val title: String?,
        val description: String?,
        val date: LocalDateTime?,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<UUID>?
    )
}
