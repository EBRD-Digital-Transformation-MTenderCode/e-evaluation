package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.CreateUnsuccessfulAwardsData
import com.procurement.evaluation.infrastructure.handler.v1.model.request.CreateUnsuccessfulAwardsRequest

fun CreateUnsuccessfulAwardsRequest.convert() = CreateUnsuccessfulAwardsData(
    lots = this.lots.map { lot ->
        CreateUnsuccessfulAwardsData.Lot(
            id = lot.id
        )
    }
)
