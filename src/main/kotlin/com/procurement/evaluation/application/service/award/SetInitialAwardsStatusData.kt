package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.lot.LotId

data class SetInitialAwardsStatusData(
    val lotId: LotId
)