package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotNull

data class Lot @JsonCreator constructor(

        @field:NotNull
        val id: String
)