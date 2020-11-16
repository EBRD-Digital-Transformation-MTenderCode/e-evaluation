package com.procurement.evaluation.domain.model

import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result
import java.util.*

typealias Owner = UUID

fun String.tryOwner(): Result<Owner, Failure.Incident.Transform.Parsing> =
    this.tryUUID()
