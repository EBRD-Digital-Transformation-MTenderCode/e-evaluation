package com.procurement.evaluation.application.service.award

import java.util.*

data class CreateUnsuccessfulAwardsData(
    val lots: List<Lot>
) {
    data class Lot(
        val id: UUID
    )
}
