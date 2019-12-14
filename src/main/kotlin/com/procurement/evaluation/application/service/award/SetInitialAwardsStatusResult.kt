package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime

data class SetInitialAwardsStatusResult(
    val awards: List<Award>
) {
    data class Award(
        val id: AwardId,
        val date: LocalDateTime,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedBid: BidId
    )
}
