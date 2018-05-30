package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.evaluation.model.dto.databinding.JsonDateDeserializer
import com.procurement.evaluation.model.dto.databinding.JsonDateSerializer
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Award(

        @JsonProperty("token")
        var token: String?,

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("date")
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        var date: LocalDateTime?,

        @JsonProperty("description")
        var description: String?,

        @JsonProperty("status")
        var status: Status?,

        @JsonProperty("statusDetails") @NotNull
        var statusDetails: Status,

        @JsonProperty("value") @Valid
        val value: Value?,

        @JsonProperty("relatedLots")
        val relatedLots: List<String>,

        @JsonProperty("relatedBid")
        val relatedBid: String?,

        @JsonProperty("suppliers") @Valid
        val suppliers: List<OrganizationReference>?,

        @JsonProperty("documents")
        var documents: List<Document>?
)