package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.model.award.access.CheckAccessToAwardParams
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.dto.award.access.CheckAccessToAwardRequest
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

fun CheckAccessToAwardRequest.convert(): Result<CheckAccessToAwardParams, DataErrors> =
    CheckAccessToAwardParams.tryCreate(
        cpid, ocid, token, owner, awardId
    )
