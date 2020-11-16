package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.CreatedAwardsResult
import com.procurement.evaluation.infrastructure.handler.v1.model.response.CreateAwardsResponse

fun CreatedAwardsResult.convert() = CreateAwardsResponse(
    awards = this.awards
        .map { award ->
            CreateAwardsResponse.Award(
                id = award.id,
                token = award.token
            )
        }
)
