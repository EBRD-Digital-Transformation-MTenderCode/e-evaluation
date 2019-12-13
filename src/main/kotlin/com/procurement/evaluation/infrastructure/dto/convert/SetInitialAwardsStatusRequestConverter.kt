package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.SetInitialAwardsStatusData
import com.procurement.evaluation.infrastructure.dto.award.initial.request.SetInitialAwardsStatusRequest

fun SetInitialAwardsStatusRequest.convert() = SetInitialAwardsStatusData(
    lotId = this.lotId
)
