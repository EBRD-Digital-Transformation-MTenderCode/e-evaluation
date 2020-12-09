package com.procurement.evaluation.application.model.award.unsuccessful

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseDate
import com.procurement.evaluation.application.model.parseEnum
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.enums.OperationType2
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.lot.tryLotId
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.lib.toSetBy
import java.time.LocalDateTime

class CreateUnsuccessfulAwardsParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val lotIds: List<LotId>,
    val date: LocalDateTime,
    val operationType: OperationType2
) {
    companion object {
        val allowedOperationTypes = OperationType2.allowedElements
            .filter {
                when (it) {
                    OperationType2.APPLY_QUALIFICATION_PROTOCOL,
                    OperationType2.SUBMISSION_PERIOD_END,
                    OperationType2.TENDER_OR_LOT_AMENDMENT_CONFIRMATION -> true

                    OperationType2.CREATE_AWARD,
                    OperationType2.CREATE_PCR,
                    OperationType2.CREATE_SUBMISSION,
                    OperationType2.DECLARE_NON_CONFLICT_OF_INTEREST,
                    OperationType2.LOT_CANCELLATION,
                    OperationType2.TENDER_CANCELLATION,
                    OperationType2.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
                    OperationType2.UPDATE_AWARD -> false
                }
            }
            .toSetBy { it }

        fun tryCreate(
            cpid: String,
            ocid: String,
            lotIds: List<String>,
            date: String,
            operationType: String
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

            val lotIdsParsed = lotIds.map {value ->
                value.tryLotId()
                    .mapFailure {
                        DataErrors.Validation.DataFormatMismatch(
                            name = "lotIds",
                            actualValue = value,
                            expectedFormat = "uuid"
                        )
                    }
                    .onFailure { return it }
            }

            val parsedCpid = parseCpid(value = cpid)
                .onFailure { return it }

            val parsedOcid = parseOcid(value = ocid)
                .onFailure { return it }

            val parsedDate = parseDate(value = date, attributeName = "date")
                .onFailure { return it }

            val parsedOperationType = parseEnum(
                value = operationType,
                target = OperationType2.Companion,
                allowedEnums = allowedOperationTypes,
                attributeName = "operationType"
            )
                .onFailure { return it }

            return CreateUnsuccessfulAwardsParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                lotIds = lotIdsParsed,
                date = parsedDate,
                operationType = parsedOperationType
            ).asSuccess()
        }
    }
}
