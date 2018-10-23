package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Bid
import com.procurement.evaluation.model.dto.ocds.Lot

data class CreateAwardsRq @JsonCreator constructor(

        val awardCriteria: String,

        val lots: List<Lot>,

        val bids: List<Bid>
)
