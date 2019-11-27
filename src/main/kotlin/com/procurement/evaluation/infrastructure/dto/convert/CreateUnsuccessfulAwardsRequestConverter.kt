package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.CreateUnsuccessfulAwardsData
import com.procurement.evaluation.infrastructure.dto.award.unsuccessful.request.CreateUnsuccessfulAwardsRequest

fun CreateUnsuccessfulAwardsRequest.convert() = CreateUnsuccessfulAwardsData(
    lots = this.lots.map { lot ->
        CreateUnsuccessfulAwardsData.Lot(
            id = lot.id
        )
    }
)