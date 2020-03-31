package com.procurement.evaluation.application.model.award.state

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.Result.Companion.failure
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.award.tryAwardId
import com.procurement.evaluation.domain.util.extension.mapResultPair
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

class GetAwardStateByIdsParams private constructor(
    val awardIds: List<AwardId>,
    val cpid: Cpid,
    val ocid: Ocid
) {
    companion object {
        fun tryCreate(
            awardIds: List<String>?,
            cpid: String,
            ocid: String
        ): Result<GetAwardStateByIdsParams, DataErrors> {
            val awardIdsAttribute = "awardIds"
            if (awardIds != null && awardIds.isEmpty())
                return failure(DataErrors.Validation.EmptyArray(awardIdsAttribute))

            val awardIdsParsed = awardIds
                ?.mapResultPair { awardId -> awardId.tryAwardId() }
                ?.doReturn { failPair ->
                    return failure(
                        DataErrors.Validation.DataFormatMismatch(
                            name = awardIdsAttribute,
                            expectedFormat = "uuid",
                            actualValue = failPair.element
                        )
                    )
                }.orEmpty()

            val cpidParsed = parseCpid(cpid)
                .doReturn { error -> return failure(error = error) }

            val ocidParsed = parseOcid(ocid)
                .doReturn { error -> return failure(error = error) }

            return GetAwardStateByIdsParams(
                awardIds = awardIdsParsed,
                cpid = cpidParsed,
                ocid = ocidParsed
            ).asSuccess()
        }
    }
}