package com.procurement.evaluation.domain.util.extension

import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result
import java.util.*

fun String.tryUUID(): Result<UUID, Failure.Incident.Transform.Parsing> =
    try {
        Result.success(UUID.fromString(this))
    } catch (ex: Exception) {
        Result.failure(
            Failure.Incident.Transform.Parsing(UUID::class.java.canonicalName, ex)
        )
    }
