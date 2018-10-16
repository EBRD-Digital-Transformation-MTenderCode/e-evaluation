package com.procurement.evaluation.model.dto.selections

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Lot

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateAwardsByLotsRs @JsonCreator constructor(

    val awards: List<Award>,

    val unsuccessfulLots: List<Lot>
)
