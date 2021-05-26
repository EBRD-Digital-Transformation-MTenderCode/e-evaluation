package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.finalize.FinalizeAwardsParams
import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.FinalizeAwardsRequest
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess

fun FinalizeAwardsRequest.convert(): Result<FinalizeAwardsParams, DataErrors> {
    val parsedCpid = parseCpid(cpid).onFailure { return it }
    val parsedOcid = parseOcid(ocid).onFailure { return it }

    return FinalizeAwardsParams(
        cpid = parsedCpid,
        ocid = parsedOcid,
        contracts = contracts.map { it.convert().onFailure { return it } }
    ).asSuccess()
}

private fun FinalizeAwardsRequest.Contract.convert(): Result<FinalizeAwardsParams.Contract, DataErrors> {
    val parsedAwardId = AwardId.fromString(awardId)

    return FinalizeAwardsParams.Contract(id = id, awardId = parsedAwardId).asSuccess()
}
