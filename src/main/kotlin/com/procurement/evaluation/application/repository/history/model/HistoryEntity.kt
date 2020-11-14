package com.procurement.evaluation.application.repository.history.model

import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import java.util.*

data class HistoryEntity(
    val commandId: CommandId,
    val command: String,
    val operationDate: Date,
    val jsonData: String
)


