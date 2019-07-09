package com.procurement.evaluation.infrastructure.tools

import com.procurement.evaluation.infrastructure.bind.date.JsonDateTimeFormatter
import java.time.LocalDateTime

fun String.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.parse(this, JsonDateTimeFormatter.formatter)
}