package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.CreateUnsuccessfulAwardsResult
import com.procurement.evaluation.infrastructure.dto.award.unsuccessful.response.CreateUnsuccessfulAwardsResponse

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
