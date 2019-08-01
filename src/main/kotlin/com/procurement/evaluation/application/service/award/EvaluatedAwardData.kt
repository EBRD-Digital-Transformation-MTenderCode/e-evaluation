package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.DocumentType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class EvaluatedAwardData(val award: Award) {
    data class Award(
        val id: UUID,
        val date: LocalDateTime,
        val description: String?,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<UUID>,
        val value: Value,
        val suppliers: List<Supplier>,
        val documents: List<Document>?
    ) {

        data class Value(
            val amount: BigDecimal,
            val currency: String
        )

        data class Supplier(
            val id: String,
            val name: String
        )

        data class Document(
            val documentType: DocumentType,
            val id: UUID,
            val title: String?,
            val description: String?,
            val relatedLots: List<UUID>
        )
    }
}
