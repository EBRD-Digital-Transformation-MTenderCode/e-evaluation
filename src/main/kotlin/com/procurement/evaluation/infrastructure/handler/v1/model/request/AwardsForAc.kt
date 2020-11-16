package com.procurement.evaluation.infrastructure.handler.v1.model.request

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Award

data class AwardsForAcRq @JsonCreator constructor(

        val cans: List<CanGetAwards>
)

data class CanGetAwards @JsonCreator constructor(

        val awardId: String
)

data class AwardsForAcRs @JsonCreator constructor(

        val awards: List<Award>
)
