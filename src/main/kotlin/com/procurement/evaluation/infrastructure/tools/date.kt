package com.procurement.evaluation.infrastructure.tools

import com.procurement.evaluation.infrastructure.bind.date.JsonDateTimeFormatter
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

fun String.toLocalDateTime(): LocalDateTime = LocalDateTime.parse(this, JsonDateTimeFormatter.formatter)

fun Date.toLocalDateTime(): LocalDateTime = this.toInstant().atZone(ZoneOffset.UTC).toLocalDateTime()
