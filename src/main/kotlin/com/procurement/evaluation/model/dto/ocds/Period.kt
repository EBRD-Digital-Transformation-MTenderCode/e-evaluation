package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Period(

        @field:NotNull
        val startDate: LocalDateTime,

        @field:NotNull
        val endDate: LocalDateTime?
)
