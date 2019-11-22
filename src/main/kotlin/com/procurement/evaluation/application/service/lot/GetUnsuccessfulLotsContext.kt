package com.procurement.evaluation.application.service.lot

import com.procurement.evaluation.domain.model.ProcurementMethod

data class GetUnsuccessfulLotsContext(
    val country: String,
    val pmd: ProcurementMethod
)
