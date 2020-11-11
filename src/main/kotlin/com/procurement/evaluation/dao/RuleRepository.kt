package com.procurement.evaluation.dao

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.infrastructure.fail.Fail

interface RuleRepository {
    fun find(
        country: String,
        pmd: ProcurementMethod,
        parameter: String
    ): Result<String?, Fail.Incident.Database.DatabaseInteractionIncident>
}
