package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.start.awardperiod.StartAwardPeriodParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.StartAwardPeriodRequest
import com.procurement.evaluation.lib.functional.Result

fun StartAwardPeriodRequest.convert(): Result<StartAwardPeriodParams, DataErrors> =
    StartAwardPeriodParams.tryCreate(cpid = this.cpid, ocid = this.ocid, date = this.date)
