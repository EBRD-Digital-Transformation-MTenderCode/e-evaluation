package com.procurement.evaluation.application.repository.rule

import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.domain.model.enums.OperationType
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result

interface RuleRepository {
    fun find(
        country: String,
        pmd: ProcurementMethod,
        operationType: OperationType? = null,
        parameter: String
    ): Result<String?, Fail.Incident.Database>
}
