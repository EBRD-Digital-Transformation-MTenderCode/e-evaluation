package com.procurement.evaluation.domain.model.requirement.response

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.infrastructure.fail.Fail

typealias RespondererId = String

fun String.tryRespondererId(): Result<RespondererId, Fail.Incident.Transform.Parsing> = this.asSuccess()
