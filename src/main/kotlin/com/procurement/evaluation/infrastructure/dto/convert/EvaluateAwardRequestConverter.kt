package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.EvaluateAwardData
import com.procurement.evaluation.infrastructure.dto.award.evaluate.request.EvaluateAwardRequest

fun EvaluateAwardRequest.convert() = EvaluateAwardData(
    award = this.award
        .let { award ->
            EvaluateAwardData.Award(
                statusDetails = award.statusDetails,
                description = award.description,
                documents = award.documents
                    ?.map { document ->
                        EvaluateAwardData.Award.Document(
                            id = document.id,
                            title = document.title,
                            description = document.description,
                            relatedLots = document.relatedLots
                                ?.toList(),
                            documentType = document.documentType
                        )
                    }
            )
        }
)