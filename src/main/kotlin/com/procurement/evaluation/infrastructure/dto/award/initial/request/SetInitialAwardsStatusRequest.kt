package com.procurement.evaluation.infrastructure.dto.award.initial.request

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.domain.model.lot.LotId

data class SetInitialAwardsStatusRequest(
    @field:JsonProperty("lotId") @param:JsonProperty("lotId") val lotId: LotId
)
