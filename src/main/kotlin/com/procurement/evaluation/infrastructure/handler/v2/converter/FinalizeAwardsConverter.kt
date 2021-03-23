package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.finalize.FinalizeAwardsParams
import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseLotId
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.FinalizeAwardsRequest
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess

fun FinalizeAwardsRequest.convert(): Result<FinalizeAwardsParams, DataErrors> {
    val parsedCpid = parseCpid(cpid)
        .onFailure { return it }
    val parsedOcid = parseOcid(ocid)
        .onFailure { return it }

    return FinalizeAwardsParams(
        cpid = parsedCpid,
        ocid = parsedOcid,
        tender = tender.convert()
            .onFailure { return it }
    ).asSuccess()
}

private fun FinalizeAwardsRequest.Tender.convert(): Result<FinalizeAwardsParams.Tender, DataErrors> =
    FinalizeAwardsParams.Tender(
        lots = lots.map { it.convert().onFailure { return it } }
    ).asSuccess()

private fun FinalizeAwardsRequest.Tender.Lot.convert(): Result<FinalizeAwardsParams.Tender.Lot, DataErrors> =
    FinalizeAwardsParams.Tender.Lot(
        id = parseLotId(id, "tender.lots.id").onFailure { return it }
    ).asSuccess()

