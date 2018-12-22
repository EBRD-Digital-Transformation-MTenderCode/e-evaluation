package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Item

data class AwardsForAcRq @JsonCreator constructor(

        val cans: List<CanGetAwards>
)

data class CanGetAwards @JsonCreator constructor(

        val awardId: String
)

data class AwardsForAcRs @JsonCreator constructor(

        val awards: List<Award>
)