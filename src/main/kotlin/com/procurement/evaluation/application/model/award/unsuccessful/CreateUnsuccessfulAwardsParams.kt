package com.procurement.evaluation.application.model.award.unsuccessful

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseDate
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asFailure
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.lot.tryLotId
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.toSetBy
import java.time.LocalDateTime

data class CreateUnsuccessfulAwardsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val lotIds: List<LotId>,
    val date: LocalDateTime
) {
    companion object {
        fun tryCreate(
            cpid: String,
            ocid: String,
            lotIds: List<String>,
            date: String
        ): Result<CreateUnsuccessfulAwardsParams, DataErrors> {

            if (lotIds.isEmpty())
                return Result.failure(DataErrors.Validation.EmptyArray(name = "lotIds"))

            val uniqueLotsIds = lotIds
                .toSetBy { it }
                .toList()

            val nonUniqueIds = lotIds - uniqueLotsIds
            if (nonUniqueIds.isNotEmpty())
                return Result.failure(
                    DataErrors.Validation.UniquenessDataMismatch(
                        name = "lotIds",
                        value = nonUniqueIds.joinToString { it }
                    )
                )

            val lotIdsParsed = lotIds.map {
                it.tryLotId()
                    .doReturn { error ->
                        return DataErrors.Validation.DataFormatMismatch(
                            name = "lotIds",
                            actualValue = it,
                            expectedFormat = "uuid"
                        ).asFailure()
                    }
            }

            val parsedCpid = parseCpid(value = cpid)
                .doReturn { error -> return error.asFailure() }

            val parsedOcid = parseOcid(value = ocid)
                .doReturn { error -> return error.asFailure() }

            val parsedDate = parseDate(value = date, attributeName = "date")
                .doReturn { error -> return error.asFailure() }

            return CreateUnsuccessfulAwardsParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                lotIds = lotIdsParsed,
                date = parsedDate
            ).asSuccess()
        }
    }
}
