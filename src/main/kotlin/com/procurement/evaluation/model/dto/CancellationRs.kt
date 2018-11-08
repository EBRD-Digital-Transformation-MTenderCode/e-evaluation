package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Lot


@JsonInclude(JsonInclude.Include.NON_NULL)
data class CancellationRq(

        val lots: List<Lot>

)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CancellationRs(

        val awards: List<Award>

)