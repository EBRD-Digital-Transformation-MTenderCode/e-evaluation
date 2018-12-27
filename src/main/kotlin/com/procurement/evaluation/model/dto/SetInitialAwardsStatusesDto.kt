package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SetInitialAwardsStatusesRq(

        val lotId: String
)

data class SetInitialAwardsStatusesRs(

        val awards: List<Award>
)

