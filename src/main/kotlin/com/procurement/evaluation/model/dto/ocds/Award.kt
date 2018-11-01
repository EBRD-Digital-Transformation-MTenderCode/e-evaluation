package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Award @JsonCreator constructor(

        var token: String?,

        val id: String,

        var date: LocalDateTime?,

        var description: String?,

        var title: String?,

        var status: Status,

        var statusDetails: Status,

        val value: Value?,

        val relatedLots: List<String>,

        val relatedBid: String?,

        val suppliers: List<OrganizationReference>?,

        var documents: List<Document>?,

        var items: List<Item>?
)