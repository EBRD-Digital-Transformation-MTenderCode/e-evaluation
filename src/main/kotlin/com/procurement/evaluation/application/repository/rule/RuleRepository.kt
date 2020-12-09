package com.procurement.evaluation.application.repository.rule

import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result

interface RuleRepository {
    fun find(
        country: String,
        pmd: ProcurementMethod,
        operationType: String? = null,
        parameter: String
    ): Result<String?, Failure.Incident.Database>
}
