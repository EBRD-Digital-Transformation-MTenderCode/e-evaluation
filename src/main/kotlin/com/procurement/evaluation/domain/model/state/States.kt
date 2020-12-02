package com.procurement.evaluation.domain.model.state

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails

class States(states: List<State>) : List<States.State> by states {
    data class State(
        @param:JsonProperty("status") @field:JsonProperty("status") val status: AwardStatus,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails?
    )
}
