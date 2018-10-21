package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Award @JsonCreator constructor(

        var token: String?,

        @field:NotNull
        val id: String,

        var date: LocalDateTime?,

        var description: String?,

        var title: String?,

        var status: Status,

        @field:NotNull
        var statusDetails: Status,

        @field:Valid
        val value: Value?,

        val relatedLots: List<String>,

        val relatedBid: String?,

        @field:Valid
        val suppliers: List<OrganizationReference>?,

        var documents: List<Document>?,

        var items: List<Item>?
)