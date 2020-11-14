package com.procurement.evaluation.domain.model.requirement.response

import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result
import java.util.*

typealias RequirementResponseId = UUID

fun String.tryRequirementResponseId(): Result<RequirementResponseId, Fail.Incident.Transform.Parsing> =
    this.tryUUID()


