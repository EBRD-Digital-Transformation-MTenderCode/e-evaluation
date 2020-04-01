package com.procurement.evaluation.domain.util.extension

import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

fun nowDefaultUTC(): LocalDateTime = ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime()
