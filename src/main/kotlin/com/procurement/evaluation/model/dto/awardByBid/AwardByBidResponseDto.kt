package com.procurement.evaluation.model.dto.awardByBid

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardByBidResponseDto(

    val awards: List<Award>,

    val bidId: String,

    val lotId: String?,

    val lotAwarded: Boolean?

)
