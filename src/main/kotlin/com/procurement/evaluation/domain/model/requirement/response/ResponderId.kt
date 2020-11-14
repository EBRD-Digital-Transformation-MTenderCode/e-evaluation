package com.procurement.evaluation.domain.model.requirement.response

import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess

typealias ResponderId = String

fun String.tryResponderId(): Result<ResponderId, Fail.Incident.Transform.Parsing> = this.asSuccess()
