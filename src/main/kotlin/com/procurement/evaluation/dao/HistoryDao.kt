package com.procurement.evaluation.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.insertInto
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.evaluation.infrastructure.dto.ApiSuccessResponse
import com.procurement.evaluation.model.entity.HistoryEntity
import com.procurement.evaluation.utils.localNowUTC
import com.procurement.evaluation.utils.toDate
import com.procurement.evaluation.utils.toJson
import org.springframework.stereotype.Service

@Service
class HistoryDao(private val session: Session) {

    fun getHistory(operationId: String, command: String): HistoryEntity? {
        val query = select()
                .all()
                .from(HISTORY_TABLE)
                .where(eq(COMMAND_ID, operationId))
                .and(eq(COMMAND_NAME, command))
                .limit(1)
        val row = session.execute(query).one()
        return if (row != null) HistoryEntity(
            row.getString(COMMAND_ID),
            row.getString(COMMAND_NAME),
            row.getTimestamp(COMMAND_DATE),
            row.getString(JSON_DATA)) else null
    }

    fun saveHistory(operationId: String, command: String, response: ApiSuccessResponse): HistoryEntity {
        val entity = HistoryEntity(
                operationId = operationId,
                command = command,
                operationDate = localNowUTC().toDate(),
                jsonData = toJson(response))

        val insert = insertInto(HISTORY_TABLE)
                .value(COMMAND_ID, entity.operationId)
                .value(COMMAND_NAME, entity.command)
                .value(COMMAND_DATE, entity.operationDate)
                .value(JSON_DATA, entity.jsonData)
        session.execute(insert)
        return entity
    }

    companion object {
        private const val HISTORY_TABLE = "evaluation_history"
        private const val COMMAND_ID = "command_id"
        private const val COMMAND_NAME = "command_name"
        private const val COMMAND_DATE = "command_date"
        private const val JSON_DATA = "json_data"
    }

}
