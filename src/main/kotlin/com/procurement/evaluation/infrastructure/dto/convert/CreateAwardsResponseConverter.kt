package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.CreatedAwardsResult
import com.procurement.evaluation.infrastructure.dto.award.create.response.CreateAwardsResponse

fun CreatedAwardsResult.convert() = CreateAwardsResponse(
    awards = this.awards
        .map { award ->
            CreateAwardsResponse.Award(
                id = award.id,
                token = award.token
            )
        }
)
