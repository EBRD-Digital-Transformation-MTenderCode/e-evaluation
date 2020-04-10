package com.procurement.evaluation.domain.model

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Fail
import java.util.*

typealias Owner = UUID

fun String.tryOwner(): Result<Owner, Fail.Incident.Transform.Parsing> =
    this.tryUUID()
