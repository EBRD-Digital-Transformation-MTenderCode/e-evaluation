package com.procurement.evaluation.application.service.award

data class AwardCancellationData(
    val lots: List<Lot>
) {
    data class Lot(
        val id: String
    )
}
