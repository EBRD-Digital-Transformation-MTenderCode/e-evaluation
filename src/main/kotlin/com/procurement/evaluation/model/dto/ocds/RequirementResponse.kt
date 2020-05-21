package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.domain.model.person.PersonId
import com.procurement.evaluation.domain.model.requirement.RequirementId
import com.procurement.evaluation.domain.model.requirement.response.RequirementResponseId
import com.procurement.evaluation.domain.model.tenderer.TendererId

data class RequirementResponse(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementResponseId,
    @field:JsonProperty("value") @param:JsonProperty("value") val value: RequirementRsValue,
    @field:JsonProperty("relatedTenderer") @param:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer,
    @field:JsonProperty("requirement") @param:JsonProperty("requirement") val requirement: Requirement,
    @field:JsonProperty("responder") @param:JsonProperty("responder") val responder: Responder
) {
    data class RelatedTenderer(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: TendererId
    )

    data class Requirement(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: RequirementId
    )

    data class Responder(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: PersonId,
        @param:JsonProperty("name") @field:JsonProperty("name") val name: String
    )
}
