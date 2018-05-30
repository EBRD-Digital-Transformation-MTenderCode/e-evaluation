package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.model.dto.ocds.Award
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class UpdateAwardRequestDto(

        @JsonProperty("award") @Valid @NotNull
        val award: Award
)
