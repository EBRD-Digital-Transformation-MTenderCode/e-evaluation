package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.StartConsiderationResult
import com.procurement.evaluation.infrastructure.handler.v1.model.response.StartConsiderationResponse

fun StartConsiderationResult.convert() = StartConsiderationResponse(
    award = this.award.let { award ->
        StartConsiderationResponse.Award(
            id = award.id,
            statusDetails = award.statusDetails,
            relatedBid = award.relatedBid
        )
    }
)
