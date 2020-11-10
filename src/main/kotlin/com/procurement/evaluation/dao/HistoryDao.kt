package com.procurement.evaluation.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.eq
import com.datastax.driver.core.querybuilder.QueryBuilder.insertInto
import com.datastax.driver.core.querybuilder.QueryBuilder.select
import com.procurement.evaluation.infrastructure.dto.ApiSuccessResponse
import com.procurement.evaluation.infrastructure.repository.Database
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
            .from(Database.History.TABLE)
            .where(eq(Database.History.COMMAND_ID, operationId))
            .and(eq(Database.History.COMMAND_NAME, command))
            .limit(1)
        val row = session.execute(query).one()
        return if (row != null) HistoryEntity(
            row.getString(Database.History.COMMAND_ID),
            row.getString(Database.History.COMMAND_NAME),
            row.getTimestamp(Database.History.COMMAND_DATE),
            row.getString(Database.History.JSON_DATA)
        ) else null
    }

    fun saveHistory(operationId: String, command: String, response: ApiSuccessResponse): HistoryEntity {
        val entity = HistoryEntity(
            operationId = operationId,
            command = command,
            operationDate = localNowUTC().toDate(),
            jsonData = toJson(response)
        )

        val insert = insertInto(Database.History.TABLE)
            .value(Database.History.COMMAND_ID, entity.operationId)
            .value(Database.History.COMMAND_NAME, entity.command)
            .value(Database.History.COMMAND_DATE, entity.operationDate)
            .value(Database.History.JSON_DATA, entity.jsonData)
        session.execute(insert)
        return entity
    }
}
