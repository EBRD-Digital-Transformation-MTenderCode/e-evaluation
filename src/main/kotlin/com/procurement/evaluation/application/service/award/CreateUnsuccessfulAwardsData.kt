package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.lot.LotId

data class CreateUnsuccessfulAwardsData(
    val lots: List<Lot>
) {
    data class Lot(
        val id: LotId
    )
}
