package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.domain.model.requirement.RequirementId
import com.procurement.evaluation.domain.model.requirement.response.RequirementResponseId
import com.procurement.evaluation.domain.model.requirement.response.RespondererId
import com.procurement.evaluation.domain.model.tenderer.TendererId
import java.time.LocalDateTime

data class Award @JsonCreator constructor(

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var token: String?,

    val id: String,

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
) {
    data class RequirementResponse(
        val id: RequirementResponseId,
        val value: RequirementRsValue,
        val relatedTenderer: RelatedTenderer,
        val requirement: Requirement,
        val responderer: Responderer
    ) {
        data class RelatedTenderer(
            val id: TendererId
        )

        data class Requirement(
            val id: RequirementId
        )

        data class Responderer(
            val id: RespondererId,
            val name: String
        )
    }
}