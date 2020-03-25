package com.procurement.evaluation.infrastructure.dto.award.create.requirement.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.data.RequirementRsValue

data class CreateRequirementResponseRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("award") @field:JsonProperty("award") val award: Award
) {
    data class Award(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("owner") @field:JsonProperty("owner") val owner: String,
        @param:JsonProperty("token") @field:JsonProperty("token") val token: String,
        @param:JsonProperty("requirementResponse") @field:JsonProperty("requirementResponse") val requirementResponse: RequirementResponse
    ) {
        data class RequirementResponse(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementRsValue,
            @param:JsonProperty("relatedTenderer") @field:JsonProperty("relatedTenderer") val relatedTenderer: RelatedTenderer,
            @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,
            @param:JsonProperty("responderer") @field:JsonProperty("responderer") val responderer: Responderer
        ) {
            data class RelatedTenderer(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            )

            data class Requirement(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            )

            data class Responderer(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String
            )
        }
    }
}