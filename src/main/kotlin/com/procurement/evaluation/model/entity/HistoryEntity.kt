package com.procurement.evaluation.model.entity

import java.util.*

data class HistoryEntity(

        var operationId: String,

        var command: String,

        var operationDate: Date,

        var jsonData: String
)


