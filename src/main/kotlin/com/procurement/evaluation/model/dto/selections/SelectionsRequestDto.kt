package com.procurement.evaluation.model.dto.selections

import com.fasterxml.jackson.annotation.JsonCreator
import com.procurement.evaluation.model.dto.ocds.Bid
import com.procurement.evaluation.model.dto.ocds.Lot
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class SelectionsRequestDto @JsonCreator constructor(

        @field:Valid @field:NotNull
        val lots: List<Lot>,

        @field:Valid @field:NotNull
        val bids: List<Bid>
)
