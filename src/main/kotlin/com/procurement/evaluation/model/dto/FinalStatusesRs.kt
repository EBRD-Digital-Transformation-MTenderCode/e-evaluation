package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Lot

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FinalStatusesRs(

        val awards: List<Award>,

        val activeAwards: List<Award>,

        val unsuccessfulLots: List<Lot>?
)
