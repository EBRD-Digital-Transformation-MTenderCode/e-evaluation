package com.procurement.evaluation.application.model.award.get

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.Result.Companion.failure
import com.procurement.evaluation.lib.functional.asSuccess

class GetAwardByIdsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val awards: List<Award>,
) {
    companion object {
        private const val AWARD_ATTRIBUTE_NAME = "awards"

        fun tryCreate(
            cpid: String,
            ocid: String,
            awards: List<Award>
        ): Result<GetAwardByIdsParams, DataErrors> {

            if (awards.isEmpty())
                return failure(DataErrors.Validation.EmptyArray(AWARD_ATTRIBUTE_NAME))

            val cpidParsed = parseCpid(cpid)
                .onFailure { return it }

            val ocidParsed = parseOcid(ocid)
                .onFailure { return it }

            return GetAwardByIdsParams(awards = awards, cpid = cpidParsed, ocid = ocidParsed).asSuccess()
        }
    }

    data class Award(
        val id: String
    )
}
