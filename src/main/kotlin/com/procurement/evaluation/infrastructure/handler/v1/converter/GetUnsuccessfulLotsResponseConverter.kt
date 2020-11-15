package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.lot.GetUnsuccessfulLotsResult
import com.procurement.evaluation.infrastructure.handler.v1.model.response.GetUnsuccessfulLotsResponse

fun GetUnsuccessfulLotsResult.convert() = GetUnsuccessfulLotsResponse(
    lots = this.lots.map { lot ->
        GetUnsuccessfulLotsResponse.Lot(
            id = lot.id
        )
    }
)
