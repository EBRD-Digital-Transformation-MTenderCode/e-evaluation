package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotNull

data class Lot(

        @JsonProperty("id") @NotNull
        val id: String
)