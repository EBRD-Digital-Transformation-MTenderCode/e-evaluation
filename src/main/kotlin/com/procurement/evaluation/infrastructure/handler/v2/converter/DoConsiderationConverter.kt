package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.consideration.DoConsiderationParams
import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.DoConsiderationRequest
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess

fun DoConsiderationRequest.convert(): Result<DoConsiderationParams, DataErrors> {
    val parsedCpid = parseCpid(cpid)
        .onFailure { return it }
    val parsedOcid = parseOcid(ocid)
        .onFailure { return it }

    return DoConsiderationParams(
        cpid = parsedCpid,
        ocid = parsedOcid,
        awards = awards.map { DoConsiderationParams.Award(it.id)}
    ).asSuccess()
}

