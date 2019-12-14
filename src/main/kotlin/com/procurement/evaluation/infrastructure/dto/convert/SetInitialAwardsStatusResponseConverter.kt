package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.SetInitialAwardsStatusResult
import com.procurement.evaluation.infrastructure.dto.award.initial.response.SetInitialAwardsStatusResponse

fun SetInitialAwardsStatusResult.convert() =
    SetInitialAwardsStatusResponse(
        awards = this.awards.map { award ->
            SetInitialAwardsStatusResponse.Award(
                id = award.id,
                date = award.date,
                status = award.status,
                statusDetails = award.statusDetails,
                relatedBid = award.relatedBid
            )
        }
    )
