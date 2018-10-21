package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Item
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class AwardsForCansRq @JsonCreator constructor(

        @field:Valid @field:NotNull
        val items: List<Item>
)

data class AwardsForCansRs @JsonCreator constructor(

        val awards: List<AwardForCan>
)

data class AwardForCan @JsonCreator constructor(

        val id: String,

        val items: List<Item>
)