package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SetInitialAwardsStatusesRq(

    val can: SetInitialCanRq
)

data class SetInitialCanRq(
    val awardId: String
)

data class SetInitialAwardsStatusesRs(
    val awards: List<Award>,
    val firsBids: FirstBid,
    val lotId: String
)

