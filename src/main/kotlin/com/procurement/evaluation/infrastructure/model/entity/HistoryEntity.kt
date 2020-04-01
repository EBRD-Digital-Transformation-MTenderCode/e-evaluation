package com.procurement.evaluation.infrastructure.model.entity

import java.util.*

data class HistoryEntity(
    val operationId: String,
    val command: String,
    val operationDate: Date,
    val jsonData: String
)


