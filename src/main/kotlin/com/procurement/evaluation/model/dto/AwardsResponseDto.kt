package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Period

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardsResponseDto(

        val awards: List<Award>,

        val awardPeriod: Period?,

        val unsuccessfulLots: List<Lot>?
)
