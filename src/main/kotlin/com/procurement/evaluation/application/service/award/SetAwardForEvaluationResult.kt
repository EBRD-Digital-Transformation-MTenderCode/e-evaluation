package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime

data class SetAwardForEvaluationResult(
    val awards: List<Award>
) {

    data class Award(
        val id: AwardId,
        val token: Token,
        val title: String?,
        val date: LocalDateTime,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<LotId>,
        val relatedBid: BidId?,
        val value: Money?,
        val suppliers: List<Supplier>,
        val weightedValue: Money?
    ) {

        data class Supplier(
            val id: String,
            val name: String
        )
    }
}
