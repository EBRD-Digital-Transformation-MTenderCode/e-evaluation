package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime

data class CreateUnsuccessfulAwardsResult(
    val awards: List<Award>
) {
    data class Award(
        val id: AwardId,
        val token: Token,
        val date: LocalDateTime,
        val title: String,
        val description: String,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<LotId>
    )
}
