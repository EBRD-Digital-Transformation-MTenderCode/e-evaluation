package com.procurement.evaluation.domain.model.requirement.response

import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess

typealias ResponderId = String

fun String.tryResponderId(): Result<ResponderId, Failure.Incident.Transform.Parsing> = this.asSuccess()
