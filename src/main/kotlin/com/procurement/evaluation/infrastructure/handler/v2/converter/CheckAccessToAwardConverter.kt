package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.access.CheckAccessToAwardParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CheckAccessToAwardRequest
import com.procurement.evaluation.lib.functional.Result

fun CheckAccessToAwardRequest.convert(): Result<CheckAccessToAwardParams, DataErrors> =
    CheckAccessToAwardParams.tryCreate(cpid = cpid, ocid = ocid, token = token, owner = owner, awardId = awardId)
