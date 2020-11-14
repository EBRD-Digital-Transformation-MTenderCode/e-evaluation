package com.procurement.evaluation.application.model.award.state

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.award.tryAwardId
import com.procurement.evaluation.domain.util.extension.mapResultPair
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.Result.Companion.failure
import com.procurement.evaluation.lib.functional.asSuccess

class GetAwardStateByIdsParams private constructor(
    val awardIds: List<AwardId>,
    val cpid: Cpid,
    val ocid: Ocid
) {
    companion object {
        fun tryCreate(
            awardIds: List<String>,
            cpid: String,
            ocid: String
        ): Result<GetAwardStateByIdsParams, DataErrors> {
            val awardIdsAttribute = "awardIds"
            if (awardIds.isEmpty())
                return failure(DataErrors.Validation.EmptyArray(awardIdsAttribute))

            val awardIdsParsed = awardIds
                .mapResultPair { awardId -> awardId.tryAwardId() }
                .mapFailure {
                    DataErrors.Validation.DataFormatMismatch(
                        name = awardIdsAttribute,
                        expectedFormat = "uuid",
                        actualValue = it.element
                    )
                }
                .onFailure { return it }

            val cpidParsed = parseCpid(cpid)
                .onFailure { return it }

            val ocidParsed = parseOcid(ocid)
                .onFailure { return it }

            return GetAwardStateByIdsParams(
                awardIds = awardIdsParsed,
                cpid = cpidParsed,
                ocid = ocidParsed
            ).asSuccess()
        }
    }
}