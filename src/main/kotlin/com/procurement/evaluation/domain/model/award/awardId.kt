package com.procurement.evaluation.domain.model.award

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Fail
import java.util.*

typealias AwardId = UUID

fun String.tryAwardId(): Result<AwardId, Fail.Incident.Parsing> =
    this.tryUUID()
