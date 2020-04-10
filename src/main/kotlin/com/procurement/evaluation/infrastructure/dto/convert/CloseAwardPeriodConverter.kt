package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.model.award.close.awardperiod.CloseAwardPeriodParams
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.close.awardperiod.CloseAwardPeriodRequest

fun CloseAwardPeriodRequest.convert(): Result<CloseAwardPeriodParams, DataErrors> =
    CloseAwardPeriodParams.tryCreate(cpid = this.cpid, ocid = this.ocid, endDate = this.endDate)
