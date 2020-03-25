package com.procurement.evaluation.application.model.award.requirement.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.domain.model.requirement.RequirementId
import com.procurement.evaluation.domain.model.requirement.response.RequirementResponseId
import com.procurement.evaluation.domain.model.requirement.response.RespondererId
import com.procurement.evaluation.domain.model.tenderer.TendererId
import com.procurement.evaluation.infrastructure.bind.criteria.RequirementValueDeserializer
import com.procurement.evaluation.infrastructure.bind.criteria.RequirementValueSerializer

data class CreateRequirementResponseResult(
    @param:JsonProperty("award") @field:JsonProperty("award") val award: Award
) {
    data class Award(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: AwardId,
        @param:JsonProperty("requirementResponse") @field:JsonProperty("requirementResponse") val requirementResponse: RequirementResponse
    ) {
        data class RequirementResponse(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementResponseId,

            @JsonDeserialize(using = RequirementValueDeserializer::class)
            @JsonSerialize(using = RequirementValueSerializer::class)
            @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementRsValue,
            @param:JsonProperty("relatedTenderer") @field:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer,
            @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,
            @param:JsonProperty("responderer") @field:JsonProperty("responderer") val responderer: Responderer
        ) {
            data class RelatedTenderer(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: TendererId
            )

            data class Requirement(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RequirementId
            )

            data class Responderer(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: RespondererId,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )
        }
    }
}