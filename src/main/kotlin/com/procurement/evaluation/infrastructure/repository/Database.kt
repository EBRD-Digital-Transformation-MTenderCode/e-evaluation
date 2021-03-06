package com.procurement.evaluation.infrastructure.repository

object Database {
    const val KEYSPACE = "evaluation"

    object History {
        const val TABLE_NAME = "history"
        const val COMMAND_ID = "command_id"
        const val COMMAND_NAME = "command_name"
        const val COMMAND_DATE = "command_date"
        const val JSON_DATA = "json_data"
    }

    object Rules {
        const val TABLE_NAME = "rules"
        const val COUNTRY = "country"
        const val PMD = "pmd"
        const val OPERATION_TYPE = "operation_type"
        const val PARAMETER = "parameter"
        const val VALUE = "value"
    }

    object Period {
        const val TABLE_NAME = "periods"
        const val CPID = "cpid"
        const val OCID = "ocid"
        const val START_DATE = "start_date"
        const val END_DATE = "end_date"
    }

    object Awards {
        const val TABLE_NAME = "awards"
        const val CPID = "cpid"
        const val OCID = "ocid"
        const val TOKEN_ENTITY = "token_entity"
        const val STATUS = "status"
        const val STATUS_DETAILS = "status_details"
        const val OWNER = "owner"
        const val JSON_DATA = "json_data"
    }
}
