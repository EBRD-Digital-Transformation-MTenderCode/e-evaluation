package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UpdateAwardResponseDto(

        val award: Award,

        val nextAward: Award?
)
