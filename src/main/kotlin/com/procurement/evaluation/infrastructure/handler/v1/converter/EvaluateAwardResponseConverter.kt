package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.EvaluateAwardResult
import com.procurement.evaluation.infrastructure.handler.v1.model.response.EvaluateAwardResponse

fun EvaluateAwardResult.convert() = EvaluateAwardResponse(
    award = this.award
        .let { award ->
            EvaluateAwardResponse.Award(
                id = award.id,
                date = award.date,
                description = award.description,
                status = award.status,
                statusDetails = award.statusDetails,
                relatedLots = award.relatedLots
                    .toList(),
                relatedBid = award.relatedBid,
                value = award.value.let { value ->
                    EvaluateAwardResponse.Award.Value(
                        amount = value.amount,
                        currency = value.currency!!
                    )
                },
                suppliers = award.suppliers
                    .map { supplier ->
                        EvaluateAwardResponse.Award.Supplier(
                            id = supplier.id,
                            name = supplier.name
                        )
                    },
                documents = award.documents
                    .map { document ->
                        EvaluateAwardResponse.Award.Document(
                            id = document.id,
                            title = document.title,
                            description = document.description,
                            relatedLots = document.relatedLots
                                .toList(),
                            documentType = document.documentType
                        )
                    },
                weightedValue = award.weightedValue
            )
        }
)