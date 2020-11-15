package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.CreatedAwardsAuctionEndResult
import com.procurement.evaluation.infrastructure.handler.v1.model.response.CreateAwardsAuctionEndResponse

fun CreatedAwardsAuctionEndResult.convert() = CreateAwardsAuctionEndResponse(
    awards = this.awards
        .map { award ->
            CreateAwardsAuctionEndResponse.Award(
                id = award.id,
                token = award.token
            )
        }
)
