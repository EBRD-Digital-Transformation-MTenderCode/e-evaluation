package com.procurement.evaluation.domain.model.tenderer

import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess

typealias TendererId = String

fun String.tryTendererId(): Result<TendererId, Fail.Incident.Transform.Parsing> = this.asSuccess()