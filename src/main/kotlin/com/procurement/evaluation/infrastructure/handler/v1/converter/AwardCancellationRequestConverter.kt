package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.AwardCancellationData
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.handler.v1.model.request.AwardCancellationRequest
import com.procurement.evaluation.lib.mapIfNotEmpty
import com.procurement.evaluation.lib.orThrow

fun AwardCancellationRequest.converter() = AwardCancellationData(
    lots = this.lots
        .mapIfNotEmpty { lot ->
            AwardCancellationData.Lot(id = lot.id)
        }
        .orThrow {
            ErrorException(
                error = ErrorType.IS_EMPTY,
                message = "The data contains empty list of the lots."
            )
        }
)
