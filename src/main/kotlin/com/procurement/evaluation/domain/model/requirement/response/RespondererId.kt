package com.procurement.evaluation.domain.model.requirement.response

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.infrastructure.fail.Fail

typealias ResponderId = String

fun String.tryResponderId(): Result<ResponderId, Fail.Incident.Transform.Parsing> = this.asSuccess()
