package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.document.DocumentId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.DocumentType

class EvaluateAwardData(val award: Award) {
    data class Award(
        val statusDetails: AwardStatusDetails,
        val description: String?,
        val documents: List<Document>
    ) {

        data class Document(
            val id: DocumentId,
            val title: String?,
            val description: String?,
            val relatedLots: List<LotId>,
            val documentType: DocumentType
        )
    }
}
