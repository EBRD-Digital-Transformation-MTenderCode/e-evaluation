package com.procurement.evaluation.application.service.lot

import com.procurement.evaluation.domain.model.lot.LotId

data class GetUnsuccessfulLotsResult(
    val lots: List<Lot>
) {

    data class Lot(
        val id: LotId
    )
}
