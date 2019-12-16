package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.AwardCancellationResult
import com.procurement.evaluation.infrastructure.dto.award.cancel.response.AwardCancellationResponse

fun AwardCancellationResult.convert() = AwardCancellationResponse(
    awards = this.awards
        .map { award ->
            AwardCancellationResponse.Award(
                id = award.id,
                title = award.title,
                description = award.description,
                date = award.date,
                status = award.status,
                statusDetails = award.statusDetails,
                relatedLots = award.relatedLots?.toList()
            )
        }
)
