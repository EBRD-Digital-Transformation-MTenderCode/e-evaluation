package com.procurement.evaluation.infrastructure.repository

import com.datastax.driver.core.Session
import com.procurement.evaluation.application.repository.HistoryRepository
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.infrastructure.extension.cassandra.tryExecute
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.model.entity.HistoryEntity
import com.procurement.evaluation.utils.localNowUTC
import com.procurement.evaluation.utils.toDate
import com.procurement.evaluation.utils.toJson
import org.springframework.stereotype.Repository

@Repository
class HistoryRepositoryCassandra(private val session: Session) : HistoryRepository {

    companion object {

        private const val SAVE_HISTORY_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.History.TABLE_NAME}(
                      ${Database.History.COMMAND_ID},
                      ${Database.History.COMMAND_NAME},
                      ${Database.History.COMMAND_DATE},
                      ${Database.History.JSON_DATA}
               )
               VALUES(?, ?, ?, ?)
               IF NOT EXISTS
            """

        private const val FIND_HISTORY_ENTRY_CQL = """
               SELECT ${Database.History.COMMAND_ID},
                      ${Database.History.COMMAND_NAME},
                      ${Database.History.COMMAND_DATE},
                      ${Database.History.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.History.TABLE_NAME}
                WHERE ${Database.History.COMMAND_ID}=?
                  AND ${Database.History.COMMAND_NAME}=?
               LIMIT 1
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryByCpidAndCommandCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    override fun getHistory(operationId: String, command: String): Result<HistoryEntity?, Fail.Incident.Database.DatabaseInteractionIncident> {
        val query = preparedFindHistoryByCpidAndCommandCQL.bind()
            .apply {
                setString(Database.History.COMMAND_ID, operationId)
                setString(Database.History.COMMAND_NAME, command)
            }

        return query.tryExecute(session)
            .doOnError { error -> return Result.failure(error) }
            .get
            .one()
            ?.let { row ->
                HistoryEntity(
                    row.getString(Database.History.COMMAND_ID),
                    row.getString(Database.History.COMMAND_NAME),
                    row.getTimestamp(Database.History.COMMAND_DATE),
                    row.getString(Database.History.JSON_DATA)
                )
            }
            .asSuccess()
    }

    override fun saveHistory(
        operationId: String,
        command: String,
        response: Any
    ): Result<HistoryEntity, Fail.Incident.Database.DatabaseInteractionIncident> {
        val entity = HistoryEntity(
            operationId = operationId,
            command = command,
            operationDate = localNowUTC().toDate(),
            jsonData = toJson(response)
        )

        val insert = preparedSaveHistoryCQL.bind()
            .apply {
                setString(Database.History.COMMAND_ID, entity.operationId)
                setString(Database.History.COMMAND_NAME, entity.command)
                setTimestamp(Database.History.COMMAND_DATE, entity.operationDate)
                setString(Database.History.JSON_DATA, entity.jsonData)
            }

        insert.tryExecute(session)
            .doOnError { error -> return Result.failure(error) }

        return entity.asSuccess()
    }
}
