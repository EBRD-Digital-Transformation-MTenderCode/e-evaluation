package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Lot
import com.procurement.evaluation.model.dto.ocds.Period


@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreateAwardsRs(

        val awardPeriod: Period?,

        val awards: List<Award>,

        val unsuccessfulLots: List<Lot>?,

        val firstBids: Set<FirstBid>?
)

data class FirstBid(val id: String)