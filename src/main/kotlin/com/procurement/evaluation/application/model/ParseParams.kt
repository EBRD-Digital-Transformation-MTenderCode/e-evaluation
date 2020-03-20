package com.procurement.evaluation.application.model

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

fun parseCpid(value: String): Result<Cpid, DataErrors.Validation.DataMismatchToPattern> =
    Cpid.tryCreate(cpid = value)
        .doReturn { expectedPattern ->
            return Result.failure(
                DataErrors.Validation.DataMismatchToPattern(
                    name = "cpid",
                    pattern = expectedPattern,
                    actualValue = value
                )
            )
        }
        .asSuccess()

fun parseOcid(value: String): Result<Ocid, DataErrors.Validation.DataMismatchToPattern> =
    Ocid.tryCreate(ocid = value)
        .doReturn { expectedPattern ->
            return Result.failure(
                DataErrors.Validation.DataMismatchToPattern(
                    name = "ocid",
                    pattern = expectedPattern,
                    actualValue = value
                )
            )
        }
        .asSuccess()