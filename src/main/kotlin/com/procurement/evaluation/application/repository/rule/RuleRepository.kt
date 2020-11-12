package com.procurement.evaluation.application.repository.rule

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.domain.model.enums.OperationType
import com.procurement.evaluation.infrastructure.fail.Fail

interface RuleRepository {
    fun find(
        country: String,
        pmd: ProcurementMethod,
        operationType: OperationType?,
        parameter: String
    ): Result<String?, Fail.Incident.Database>
}
