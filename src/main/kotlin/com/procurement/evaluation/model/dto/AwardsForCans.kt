package com.procurement.evaluation.model.dto.AwardsForCansRequestDto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Item
import javax.validation.Valid
import javax.validation.constraints.NotNull


data class AwardsForCansRequestDto @JsonCreator constructor(

        @field:Valid @field:NotNull
        val items: List<Item>
)


data class AwardsForCansResponseDto @JsonCreator constructor(

        val awards: List<AwardForCanDto>
)

data class AwardForCanDto @JsonCreator constructor(

        val id: String,

        val items: List<Item>
)