package com.procurement.evaluation.domain.model.award

import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result
import java.util.*

typealias AwardId = UUID

fun String.tryAwardId(): Result<AwardId, Fail.Incident.Transform.Parsing> =
    this.tryUUID()
