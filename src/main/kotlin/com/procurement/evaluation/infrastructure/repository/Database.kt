package com.procurement.evaluation.infrastructure.repository

object Database {
    const val KEYSPACE = "evaluation"

    object History {
        const val TABLE = "history"
        const val COMMAND_ID = "command_id"
        const val COMMAND_NAME = "command_name"
        const val COMMAND_DATE = "command_date"
        const val JSON_DATA = "json_data"
    }


}
