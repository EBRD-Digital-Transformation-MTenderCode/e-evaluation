package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.model.award.access.CheckAccessToAwardParams
import com.procurement.evaluation.infrastructure.dto.award.access.CheckAccessToAwardRequest
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result

fun CheckAccessToAwardRequest.convert(): Result<CheckAccessToAwardParams, DataErrors> =
    CheckAccessToAwardParams.tryCreate(
        cpid, ocid, token, owner, awardId
    )
