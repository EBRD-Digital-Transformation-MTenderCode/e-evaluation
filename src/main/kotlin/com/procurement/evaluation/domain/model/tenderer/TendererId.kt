package com.procurement.evaluation.domain.model.tenderer

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.infrastructure.fail.Fail

typealias TendererId = String

fun String.tryTendererId(): Result<TendererId, Fail.Incident.Parsing> = this.asSuccess()