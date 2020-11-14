package com.procurement.evaluation.domain.model

import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result
import java.util.*

typealias Token = UUID

fun String.tryToken(): Result<Token, Fail.Incident.Transform.Parsing> =
    this.tryUUID()
