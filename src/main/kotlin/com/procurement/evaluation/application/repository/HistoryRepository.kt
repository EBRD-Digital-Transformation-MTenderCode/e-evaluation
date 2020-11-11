package com.procurement.evaluation.application.repository

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.model.entity.HistoryEntity

interface HistoryRepository {
    fun getHistory(operationId: String, command: String): Result<HistoryEntity?, Fail.Incident.Database.DatabaseInteractionIncident>
    fun saveHistory(operationId: String, command: String, response: Any): Result<HistoryEntity, Fail.Incident.Database.DatabaseInteractionIncident>
}
