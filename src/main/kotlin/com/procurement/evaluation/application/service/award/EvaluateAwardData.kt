package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.document.DocumentId
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.DocumentType
import java.util.*

class EvaluateAwardData(val award: Award) {
    data class Award(
        val statusDetails: AwardStatusDetails,
        val description: String?,
        val documents: List<Document>?
    ) {

        data class Document(
            val id: DocumentId,
            val title: String?,
            val description: String?,
            val relatedLots: List<UUID>?,
            val documentType: DocumentType
        )
    }
}
