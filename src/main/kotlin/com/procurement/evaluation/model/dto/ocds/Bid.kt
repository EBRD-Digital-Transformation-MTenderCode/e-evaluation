package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Bid @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        val date: LocalDateTime,

        val pendingDate: LocalDateTime?,

        val createdDate: LocalDateTime?,

        @field:Valid @field:NotNull
        val value: Value,

        @field:Valid @field:NotNull @field:NotEmpty
        val tenderers: List<OrganizationReference>,

        @field:Valid @field:NotNull @field:NotEmpty
        val relatedLots: List<String>
)