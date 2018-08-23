package com.procurement.evaluation.model.dto.awardByBid

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class AwardByBidRequestDto @JsonCreator constructor(

        @field:Valid @field:NotNull
        val awards: AwardByBid
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardByBid @JsonCreator constructor(

        @field:NotNull
        var statusDetails: Status,

        var description: String?,

        var documents: List<Document>?
)