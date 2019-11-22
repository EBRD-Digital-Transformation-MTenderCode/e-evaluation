package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.lot.GetUnsuccessfulLotsData
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.dto.lot.unsuccessful.request.GetUnsuccessfulLotsRequest
import com.procurement.evaluation.lib.mapIfNotEmpty
import com.procurement.evaluation.lib.orThrow

fun GetUnsuccessfulLotsRequest.convert() = GetUnsuccessfulLotsData(
    bids = this.bids
        .mapIfNotEmpty { bid ->
            GetUnsuccessfulLotsData.Bid(
                id = bid.id,
                relatedLots = bid.relatedLots.toList()
            )
        }
        .orThrow {
            ErrorException(
                error = ErrorType.IS_EMPTY,
                message = "The data contains empty list of the bids."
            )
        },
    lots = this.lots
        .mapIfNotEmpty { lot ->
            GetUnsuccessfulLotsData.Lot(
                id = lot.id
            )
        }
        .orThrow {
            ErrorException(
                error = ErrorType.IS_EMPTY,
                message = "The data contains empty list of the lots."
            )
        }
)
