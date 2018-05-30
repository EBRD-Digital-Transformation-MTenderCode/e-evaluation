package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.model.dto.ocds.Award

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateAwardResponseDto(

        @JsonProperty("award")
        val award: Award,

        @JsonProperty("nextAward")
        val nextAward: Award?
)
