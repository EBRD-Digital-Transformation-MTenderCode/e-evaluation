package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import javax.validation.constraints.NotNull

data class Identifier @JsonCreator constructor(

        val id: String,

        val scheme: String,

        val legalName: String,

        val uri: String?
)