package com.procurement.evaluation.domain.model.requirement.response

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Fail
import java.util.*

typealias RequirementResponseId = UUID

fun String.tryRequirementResponseId(): Result<RequirementResponseId, Fail.Incident.Parsing> =
    this.tryUUID()


