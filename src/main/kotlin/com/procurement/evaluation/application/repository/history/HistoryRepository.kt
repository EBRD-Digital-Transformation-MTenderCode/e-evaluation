package com.procurement.evaluation.application.repository.history

import com.procurement.evaluation.application.repository.history.model.HistoryEntity
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.dto.Action
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result

interface HistoryRepository {
    fun getHistory(commandId: CommandId, command: Action): Result<String?, Fail.Incident.Database>
    fun saveHistory(commandId: CommandId, command: Action, response: Any): Result<HistoryEntity, Fail.Incident.Database>
}
