package com.procurement.evaluation.infrastructure.repository.history

import com.datastax.driver.core.Session
import com.procurement.evaluation.application.repository.history.HistoryRepository
import com.procurement.evaluation.application.repository.history.model.HistoryEntity
import com.procurement.evaluation.domain.util.extension.nowDefaultUTC
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.dto.Action
import com.procurement.evaluation.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.evaluation.infrastructure.extension.cassandra.tryExecute
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.repository.Database
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.utils.toJson
import org.springframework.stereotype.Repository

@Repository
class CassandraHistoryRepository(private val session: Session) : HistoryRepository {

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
            """
    }

    private val preparedSaveHistoryCQL = session.prepare(SAVE_HISTORY_CQL)
    private val preparedFindHistoryByCpidAndCommandCQL = session.prepare(FIND_HISTORY_ENTRY_CQL)

    override fun getHistory(commandId: CommandId, command: Action): Result<String?, Fail.Incident.Database> =
        preparedFindHistoryByCpidAndCommandCQL.bind()
            .apply {
                setString(Database.History.COMMAND_ID, commandId.underlying)
                setString(Database.History.COMMAND_NAME, command.key)
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.getString(Database.History.JSON_DATA)
            .asSuccess()

    override fun saveHistory(
        commandId: CommandId,
        command: Action,
        response: Any
    ): Result<HistoryEntity, Fail.Incident.Database> {
        val entity = HistoryEntity(
            commandId = commandId,
            command = command.key,
            operationDate = nowDefaultUTC().toCassandraTimestamp(),
            jsonData = toJson(response)
        )

        preparedSaveHistoryCQL.bind()
            .apply {
                setString(Database.History.COMMAND_ID, entity.commandId.underlying)
                setString(Database.History.COMMAND_NAME, entity.command)
                setTimestamp(Database.History.COMMAND_DATE, entity.operationDate)
                setString(Database.History.JSON_DATA, entity.jsonData)
            }
            .tryExecute(session)
            .doOnError { error -> return Result.failure(error) }

        return entity.asSuccess()
    }
}
