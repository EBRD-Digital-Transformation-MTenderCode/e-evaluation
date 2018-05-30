package com.procurement.evaluation.model.dto.selections

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.model.dto.ocds.Bid
import com.procurement.evaluation.model.dto.ocds.Lot
import javax.validation.Valid
import javax.validation.constraints.NotNull

data class SelectionsRequestDto(

        @JsonProperty("lots") @Valid @NotNull
        val lots: List<Lot>,

        @JsonProperty("bids") @Valid @NotNull
        val bids: List<Bid>
)
