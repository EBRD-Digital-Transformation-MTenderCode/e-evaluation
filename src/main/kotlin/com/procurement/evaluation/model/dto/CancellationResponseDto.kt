package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Status

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CancellationResponseDto(

        val awards: List<AwardCancellation>

)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardCancellation @JsonCreator constructor(

        val id: String,

        var status: Status?,

        var statusDetails: Status
)