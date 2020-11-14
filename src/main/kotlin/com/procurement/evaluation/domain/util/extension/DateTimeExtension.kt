package com.procurement.evaluation.domain.util.extension

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asFailure
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.infrastructure.fail.error.DataTimeError
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

private const val FORMAT_PATTERN = "uuuu-MM-dd'T'HH:mm:ss'Z'"
private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(FORMAT_PATTERN)
    .withResolverStyle(ResolverStyle.STRICT)

fun nowDefaultUTC(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

fun LocalDateTime.asString(): String = this.format(formatter)

fun String.toLocalDateTime(): Result<LocalDateTime, DataTimeError> = try {
    LocalDateTime.parse(this, formatter).asSuccess()
} catch (expected: Exception) {
    if (expected.cause == null)
        DataTimeError.InvalidFormat(value = this, pattern = FORMAT_PATTERN, reason = expected).asFailure()
    else
        DataTimeError.InvalidDateTime(value = this, reason = expected).asFailure()
}
