package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.StartAwardPeriodResult
import com.procurement.evaluation.infrastructure.handler.v1.model.response.StartAwardPeriodResponse

fun StartAwardPeriodResult.convert() = StartAwardPeriodResponse(
    awardPeriod = StartAwardPeriodResponse.AwardPeriod(
        startDate = this.awardPeriod.startDate
    )
)
