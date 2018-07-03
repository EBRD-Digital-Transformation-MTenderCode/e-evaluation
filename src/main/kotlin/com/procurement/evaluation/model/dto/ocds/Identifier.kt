package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotNull

data class Identifier @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        val scheme: String,

        @field:NotNull
        val legalName: String,

        @field:NotNull
        val uri: String
)