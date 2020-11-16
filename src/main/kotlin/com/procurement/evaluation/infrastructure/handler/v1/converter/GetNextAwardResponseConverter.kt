package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.GetNextAwardResult
import com.procurement.evaluation.infrastructure.handler.v1.model.response.GetNextAwardResponse

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
