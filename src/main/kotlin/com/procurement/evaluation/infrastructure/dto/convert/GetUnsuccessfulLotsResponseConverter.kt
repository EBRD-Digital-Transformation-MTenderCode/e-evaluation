package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.lot.GetUnsuccessfulLotsResult
import com.procurement.evaluation.infrastructure.dto.lot.unsuccessful.response.GetUnsuccessfulLotsResponse

fun GetUnsuccessfulLotsResult.convert() = GetUnsuccessfulLotsResponse(
    lots = this.lots.map { lot ->
        GetUnsuccessfulLotsResponse.Lot(
            id = lot.id
        )
    }
)
