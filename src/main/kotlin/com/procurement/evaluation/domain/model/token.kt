package com.procurement.evaluation.domain.model

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Fail
import java.util.*

typealias Token = UUID

fun String.tryToken(): Result<Token, Fail.Incident.Parsing> =
    this.tryUUID()
