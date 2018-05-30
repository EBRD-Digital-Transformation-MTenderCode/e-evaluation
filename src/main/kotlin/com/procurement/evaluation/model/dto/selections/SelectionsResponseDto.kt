package com.procurement.evaluation.model.dto.selections

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Period

data class SelectionsResponseDto(

        @JsonProperty("awardPeriod")
        val period: Period,

        @JsonProperty("awards")
        val awards: List<Award>,

        @JsonProperty("unsuccessfulLots")
        val unsuccessfulLots: List<Lot>
)
