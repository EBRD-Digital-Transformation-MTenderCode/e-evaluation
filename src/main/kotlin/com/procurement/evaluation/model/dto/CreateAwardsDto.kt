package com.procurement.evaluation.model.dto.selections

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Bid
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Period
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class CreateAwardsRq @JsonCreator constructor(

        val awardCriteria: String,

        @field:Valid @field:NotNull
        val lots: List<Lot>,

        @field:Valid @field:NotNull
        val bids: List<Bid>
)


data class CreateAwardsRs(

        val awardPeriod: Period,

        val awards: List<Award>,

        val unsuccessfulLots: List<Lot>
)
