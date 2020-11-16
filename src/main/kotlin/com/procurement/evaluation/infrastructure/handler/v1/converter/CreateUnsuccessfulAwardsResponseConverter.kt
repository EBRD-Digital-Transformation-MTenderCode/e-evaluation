package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.CreateUnsuccessfulAwardsResult
import com.procurement.evaluation.infrastructure.handler.v1.model.response.CreateUnsuccessfulAwardsResponse

fun CreateUnsuccessfulAwardsResult.convert() = CreateUnsuccessfulAwardsResponse(
    awards = this.awards.map { award ->
        CreateUnsuccessfulAwardsResponse.Award(
            id = award.id,
            token = award.token,
            title = award.title,
            description = award.description,
            status = award.status,
            statusDetails = award.statusDetails,
            date = award.date,
            relatedLots = award.relatedLots.toList()
        )
    }
)
