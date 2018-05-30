package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.evaluation.model.dto.databinding.JsonDateDeserializer
import com.procurement.evaluation.model.dto.databinding.JsonDateSerializer
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Bid(

        @JsonProperty("id") @NotNull
        val id: String,

        @JsonProperty("date") @NotNull
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        val date: LocalDateTime,

        @JsonProperty("pendingDate")
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        val pendingDate: LocalDateTime?,

        @JsonProperty("createdDate")
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        val createdDate: LocalDateTime?,

        @JsonProperty("value") @Valid @NotNull
        val value: Value,

        @JsonProperty("tenderers") @Valid @NotEmpty
        val tenderers: List<OrganizationReference>,

        @JsonProperty("relatedLots") @Valid @NotEmpty
        val relatedLots: List<String>
)