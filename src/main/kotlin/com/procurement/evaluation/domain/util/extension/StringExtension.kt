package com.procurement.evaluation.domain.util.extension

import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result
import java.util.*

fun String.tryUUID(): Result<UUID, Fail.Incident.Transform.Parsing> =
    try {
        Result.success(UUID.fromString(this))
    } catch (ex: Exception) {
        Result.failure(
            Fail.Incident.Transform.Parsing(UUID::class.java.canonicalName, ex)
        )
    }