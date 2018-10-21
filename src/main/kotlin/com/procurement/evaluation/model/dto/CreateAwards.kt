package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Bid
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Period

data class CreateAwardsRq @JsonCreator constructor(

        val awardCriteria: String,

        val lots: List<Lot>,

        val bids: List<Bid>
)

data class CreateAwardsRs(

        val awardPeriod: Period,

        val awards: List<Award>,

        val unsuccessfulLots: List<Lot>
)