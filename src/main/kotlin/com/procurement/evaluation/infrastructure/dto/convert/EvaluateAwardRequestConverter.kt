package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.EvaluateAwardData
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.dto.award.evaluate.request.EvaluateAwardRequest
import com.procurement.evaluation.lib.errorIfEmpty
import com.procurement.evaluation.model.dto.ocds.Value

fun EvaluateAwardRequest.convert() = EvaluateAwardData(
    award = this.award
        .let { award ->
            EvaluateAwardData.Award(
                statusDetails = award.statusDetails,
                description = award.description,
                documents = award.documents
                    .errorIfEmpty {
                        ErrorException(
                            error = ErrorType.IS_EMPTY,
                            message = "The award contains empty list of documents."
                        )
                    }
                    ?.map { document ->
                        EvaluateAwardData.Award.Document(
                            id = document.id,
                            title = document.title,
                            description = document.description,
                            relatedLots = document.relatedLots
                                .errorIfEmpty {
                                    ErrorException(
                                        error = ErrorType.IS_EMPTY,
                                        message = "The document '${document.id}' in award contains empty list of related lots."
                                    )
                                }
                                ?.toList()
                                .orEmpty(),
                            documentType = document.documentType
                        )
                    }
                    .orEmpty(),
                value = award.value?.let { Value(amount = it.amount, currency = null) }
            )
        }
)
