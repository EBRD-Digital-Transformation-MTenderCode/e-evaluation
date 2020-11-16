package com.procurement.evaluation.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class WinningAwardResponse(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @field:JsonProperty("award") @param:JsonProperty("award") val award: Award?
) {
    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID
    )
}
