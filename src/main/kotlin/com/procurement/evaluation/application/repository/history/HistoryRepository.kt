package com.procurement.evaluation.application.repository.history

import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.dto.Action
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result

interface HistoryRepository {
    fun getHistory(commandId: CommandId, action: Action): Result<String?, Fail.Incident.Database>
    fun saveHistory(commandId: CommandId, action: Action, data: String): Result<Boolean, Fail.Incident.Database>
}
