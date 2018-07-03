package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Award
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class UpdateAwardRequestDto @JsonCreator constructor(

        @field:Valid
        @field:NotNull
        val award: Award
)
