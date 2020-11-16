package com.procurement.evaluation.domain.model.requirement

import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result
import java.util.*

typealias RequirementId = UUID

fun String.tryRequirementId(): Result<RequirementId, Failure.Incident.Transform.Parsing> =
    this.tryUUID()
