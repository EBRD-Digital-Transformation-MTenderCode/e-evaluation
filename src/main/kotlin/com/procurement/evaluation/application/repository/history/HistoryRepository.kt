package com.procurement.evaluation.application.repository.history

import com.procurement.evaluation.application.repository.history.model.HistoryEntity
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.fail.Fail

interface HistoryRepository {
    fun getHistory(operationId: String, command: String): Result<HistoryEntity?, Fail.Incident.Database>
    fun saveHistory(operationId: String, command: String, response: Any): Result<HistoryEntity, Fail.Incident.Database>
}
