package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

data class Identifier @JsonCreator constructor(

        val id: String,

        val scheme: String,

        val legalName: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        val uri: String?
)
