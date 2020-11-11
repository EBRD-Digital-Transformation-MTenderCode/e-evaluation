package com.procurement.evaluation.application.repository.history.model

import java.util.*

data class HistoryEntity(
    val operationId: String,
    val command: String,
    val operationDate: Date,
    val jsonData: String
)


