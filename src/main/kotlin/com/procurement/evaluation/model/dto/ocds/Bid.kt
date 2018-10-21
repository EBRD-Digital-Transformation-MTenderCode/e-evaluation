package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Bid @JsonCreator constructor(

        val id: String,

        val date: LocalDateTime?,

        val pendingDate: LocalDateTime?,

        val createdDate: LocalDateTime?,

        var value: Value,

        val tenderers: List<OrganizationReference>,

        val relatedLots: List<String>
)