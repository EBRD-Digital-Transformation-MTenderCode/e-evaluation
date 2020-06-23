package com.procurement.evaluation.application.model

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.award.tryAwardId
import com.procurement.evaluation.domain.model.enums.EnumElementProvider
import com.procurement.evaluation.domain.model.enums.EnumElementProvider.Companion.keysAsStrings
import com.procurement.evaluation.domain.model.tryOwner
import com.procurement.evaluation.domain.model.tryToken
import com.procurement.evaluation.domain.util.extension.tryParseLocalDateTime
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import java.time.LocalDateTime

fun parseCpid(value: String): Result<Cpid, DataErrors.Validation.DataMismatchToPattern> =
    Cpid.tryCreateOrNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "cpid",
                pattern = Cpid.pattern,
                actualValue = value
            )
        )

fun parseOcid(value: String): Result<Ocid, DataErrors.Validation.DataMismatchToPattern> =
    Ocid.tryCreateOrNull(value = value)
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.DataMismatchToPattern(
                name = "ocid",
                pattern = Ocid.pattern,
                actualValue = value
            )
        )

fun parseAwardId(value: String): Result<AwardId, DataErrors.Validation.DataFormatMismatch> =
    value.tryAwardId()
        .doReturn {
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = "awardId",
                    expectedFormat = "uuid",
                    actualValue = value
                )
            )
        }
        .asSuccess()

fun parseToken(value: String): Result<Token, DataErrors.Validation.DataFormatMismatch> =
    value.tryToken()
        .doReturn {
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = "token",
                    expectedFormat = "uuid",
                    actualValue = value
                )
            )
        }.asSuccess()

fun parseOwner(value: String): Result<Owner, DataErrors.Validation.DataFormatMismatch> =
    value.tryOwner()
        .doReturn {
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = "owner",
                    expectedFormat = "uuid",
                    actualValue = value
                )
            )
        }.asSuccess()

fun parseDate(value: String, attributeName: String): Result<LocalDateTime, DataErrors.Validation.DataFormatMismatch> =
    value.tryParseLocalDateTime()
        .doReturn { pattern ->
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = attributeName,
                    actualValue = value,
                    expectedFormat = pattern
                )
            )
        }.asSuccess()

fun <T> parseEnum(value: String, allowedEnums: Set<T>, attributeName: String, target: EnumElementProvider<T>)
    : Result<T, DataErrors.Validation.UnknownValue> where T : Enum<T>,
                                                          T : EnumElementProvider.Key =
    target.orNull(value)
        ?.takeIf { it in allowedEnums }
        ?.asSuccess()
        ?: Result.failure(
            DataErrors.Validation.UnknownValue(
                name = attributeName,
                expectedValues = allowedEnums.keysAsStrings(),
                actualValue = value
            )
        )
