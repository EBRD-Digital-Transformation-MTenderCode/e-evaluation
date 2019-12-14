package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.CreatedAwardsAuctionEndResult
import com.procurement.evaluation.infrastructure.dto.award.create.auction.end.response.CreateAwardsAuctionEndResponse

fun CreatedAwardsAuctionEndResult.convert() = CreateAwardsAuctionEndResponse(
    awards = this.awards
        .map { award ->
            CreateAwardsAuctionEndResponse.Award(
                id = award.id,
                token = award.token
            )
        }
)