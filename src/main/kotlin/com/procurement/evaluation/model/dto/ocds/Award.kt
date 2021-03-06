package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class Award @JsonCreator constructor(

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var token: String?,

    val id: String,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var date: LocalDateTime?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var description: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var title: String?,

    var status: AwardStatus,

    var statusDetails: AwardStatusDetails,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val value: Value?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val weightedValue: Value?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val relatedLots: List<String>,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val relatedBid: String?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val bidDate: LocalDateTime?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val suppliers: List<OrganizationReference>?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var documents: List<Document>?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    var items: List<Item>?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val requirementResponses: List<RequirementResponse> = emptyList()
)
