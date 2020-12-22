package com.procurement.evaluation.lib

import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asFailure
import com.procurement.evaluation.lib.functional.asSuccess

inline fun <T : String?, E : RuntimeException> T.takeIfNotEmpty(error: () -> E): T =
    if (this != null && this.isBlank()) throw error() else this

fun <T> T?.takeIfNotNullOrDefault(default: T?): T? = this ?: default

inline fun <T : String?> T.errorIfBlank(error: () -> ErrorException): T =
    if (this != null && this.isBlank()) throw error() else this

inline fun String?.errorIfBlank(error: () -> DataErrors.Validation.EmptyString): Result<String?, DataErrors.Validation.EmptyString> =
    if (this != null && this.isBlank()) error().asFailure() else this.asSuccess()
