package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime
import java.util.*

data class CreateUnsuccessfulAwardsResult(
    val awards: List<Award>
) {
    data class Award(
        val id: UUID,
        val date: LocalDateTime,
        val title: String,
        val description: String,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<UUID>
    )
}