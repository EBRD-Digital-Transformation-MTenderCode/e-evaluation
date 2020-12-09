package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Document @JsonCreator constructor(

        val id: String,

        val documentType: DocumentType,

        var title: String?,

        var description: String?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        var relatedLots: List<String>?
)
