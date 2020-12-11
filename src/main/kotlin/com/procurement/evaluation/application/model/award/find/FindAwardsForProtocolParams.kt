package com.procurement.evaluation.application.model.award.find

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.Result.Companion.failure
import com.procurement.evaluation.lib.functional.asSuccess

class FindAwardsForProtocolParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val tender: Tender,
) {
    companion object {
        private const val LOTS_ATTRIBUTE_NAME = "lots"

        fun tryCreate(
            cpid: String,
            ocid: String,
            tender: Tender
        ): Result<FindAwardsForProtocolParams, DataErrors> {

            if (tender.lots.isEmpty())
                return failure(DataErrors.Validation.EmptyArray(LOTS_ATTRIBUTE_NAME))

            val cpidParsed = parseCpid(cpid)
                .onFailure { return it }

            val ocidParsed = parseOcid(ocid)
                .onFailure { return it }

            return FindAwardsForProtocolParams(cpid = cpidParsed, ocid = ocidParsed, tender = tender).asSuccess()
        }
    }

    data class Tender(
        val lots: List<Lot>
    ) {
        data class Lot(
            val id: String
        )
    }
}
