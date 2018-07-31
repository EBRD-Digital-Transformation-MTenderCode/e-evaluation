package com.procurement.evaluation.model.dto.selections

import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Period

data class SelectionsResponseDto(

        val awardPeriod: Period,

        val awards: List<Award>,

        val unsuccessfulLots: List<Lot>
)
