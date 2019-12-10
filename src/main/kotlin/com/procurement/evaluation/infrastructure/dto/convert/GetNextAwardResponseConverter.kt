package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.GetNextAwardResult
import com.procurement.evaluation.infrastructure.dto.award.next.GetNextAwardResponse

fun GetNextAwardResult.convert() = GetNextAwardResponse(
    award = this.award
        ?.let {
            GetNextAwardResponse.Award(
                id = award.id,
                statusDetails = award.statusDetails,
                relatedBid = award.relatedBid
            )
        }
)
