package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Item

data class AwardForCansRq @JsonCreator constructor(

        val items: List<Item>
)

data class AwardForCansRs @JsonCreator constructor(

        val award: AwardForCan
)

data class AwardForCan @JsonCreator constructor(

        val id: String,

        val items: List<Item>
)