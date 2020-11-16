package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.close.awardperiod.CloseAwardPeriodParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CloseAwardPeriodRequest
import com.procurement.evaluation.lib.functional.Result

fun CloseAwardPeriodRequest.convert(): Result<CloseAwardPeriodParams, DataErrors> =
    CloseAwardPeriodParams.tryCreate(cpid = this.cpid, ocid = this.ocid, endDate = this.endDate)
