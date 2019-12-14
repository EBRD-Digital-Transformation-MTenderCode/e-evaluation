package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.document.DocumentId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.DocumentType
import java.time.LocalDateTime

class EvaluateAwardResult(val award: Award) {
    data class Award(
        val id: AwardId,
        val date: LocalDateTime,
        val description: String?,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<LotId>,
        val relatedBid: BidId?,
        val value: Money,
        val suppliers: List<Supplier>,
        val documents: List<Document>,
        val weightedValue: Money?
    ) {

        data class Supplier(
            val id: String,
            val name: String
        )

        data class Document(
            val documentType: DocumentType,
            val id: DocumentId,
            val title: String?,
            val description: String?,
            val relatedLots: List<LotId>
        )
    }
}
