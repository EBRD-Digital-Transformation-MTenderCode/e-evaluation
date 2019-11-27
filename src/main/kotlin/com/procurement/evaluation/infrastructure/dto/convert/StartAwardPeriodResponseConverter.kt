package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.StartAwardPeriodResult
import com.procurement.evaluation.infrastructure.dto.award.period.start.StartAwardPeriodResponse

fun StartAwardPeriodResult.convert() = StartAwardPeriodResponse(
    awardPeriod = StartAwardPeriodResponse.AwardPeriod(
        startDate = this.awardPeriod.startDate
    )
)
