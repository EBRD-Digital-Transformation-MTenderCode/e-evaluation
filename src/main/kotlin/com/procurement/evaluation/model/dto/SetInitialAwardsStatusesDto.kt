package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SetInitialAwardsStatusesRq @JsonCreator constructor(

        val lotId: String
)

data class SetInitialAwardsStatusesRs @JsonCreator constructor(

        val awards: List<Award>
)

