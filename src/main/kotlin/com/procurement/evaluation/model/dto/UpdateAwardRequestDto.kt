package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.*
import com.procurement.evaluation.model.dto.ocds.Award
import java.time.LocalDateTime
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class UpdateAwardRequestDto @JsonCreator constructor(

        @field:Valid @field:NotNull
        val award: AwardUpdate
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardUpdate @JsonCreator constructor(

        @field:NotNull
        val id: String,

        @field:NotNull
        var statusDetails: Status,

        var description: String?,

        var documents: List<Document>?
)