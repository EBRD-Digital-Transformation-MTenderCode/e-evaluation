package com.procurement.evaluation.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.datastax.driver.core.Statement
import com.procurement.evaluation.application.exception.ReadEntityException
import com.procurement.evaluation.application.exception.SaveEntityException
import com.procurement.evaluation.application.repository.AwardPeriodRepository
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.enums.Stage
import com.procurement.evaluation.infrastructure.extension.cassandra.tryExecute
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.tools.toCassandraTimestamp
import com.procurement.evaluation.infrastructure.tools.toLocalDateTime
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CassandraAwardPeriodRepository(private val session: Session) : AwardPeriodRepository {
    companion object {
        private const val keySpace = "ocds"
        private const val tableName = "evaluation_period"
        private const val columnCpid = "cp_id"
        private const val columnStage = "stage"
        private const val columnStartDate = "start_date"
        private const val columnEndDate = "end_date"

        private const val FIND_BY_CPID_AND_STAGE_CQL = """
               SELECT $columnStartDate
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
                  AND $columnStage=?
                  LIMIT 1
            """

        private const val FIND_END_DATE_BY_CPID_AND_STAGE_CQL = """
            SELECT $columnEndDate
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
                  AND $columnStage=?
        """

        private const val SAVE_NEW_START_DATE_CQL = """
               INSERT INTO $keySpace.$tableName(
                           $columnCpid, 
                           $columnStage, 
                           $columnStartDate
               )
               VALUES(?, ?, ?) 
               IF NOT EXISTS
            """

        private const val SAVE_END_DATE_CQL = """
               UPDATE $keySpace.$tableName
                  SET $columnEndDate=?
                WHERE $columnCpid=?
                  AND $columnStage=?
               IF EXISTS
            """
    }

    private val preparedFindByCpidAndStageCQL = session.prepare(FIND_BY_CPID_AND_STAGE_CQL)
    private val preparedSaveNewStartDateCQL = session.prepare(SAVE_NEW_START_DATE_CQL)
    private val preparedSaveEndDateCQL = session.prepare(SAVE_END_DATE_CQL)
    private val preparedFindEndDateByCpidAndStageCQL = session.prepare(FIND_END_DATE_BY_CPID_AND_STAGE_CQL)

    override fun findStartDateBy(cpid: String, stage: String): LocalDateTime? {
        val query = preparedFindByCpidAndStageCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnStage, stage)
            }

        val resultSet = load(query)
        return resultSet.one()?.let {
            it.getTimestamp(columnStartDate)
                .toLocalDateTime()
        }
    }

    protected fun load(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw ReadEntityException(message = "Error read Award(s) from the database.", cause = exception)
    }

    override fun saveNewStart(cpid: String, stage: String, start: LocalDateTime) {
        val statement = preparedSaveNewStartDateCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnStage, stage)
                setTimestamp(columnStartDate, start.toCassandraTimestamp())
            }

        val result = saveNewStart(statement)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the start award period '$start' by cpid '$cpid' and stage '$stage' to the database. Record is already.")
    }

    private fun saveNewStart(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing start date of the award period.", cause = exception)
    }

    override fun saveEnd(cpid: String, stage: String, end: LocalDateTime) {
        val statement = preparedSaveEndDateCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnStage, stage)
                setTimestamp(columnEndDate, end.toCassandraTimestamp())
            }

        val result = saveEnd(statement)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the end award period '$end' by cpid '$cpid' and stage '$stage' to the database. Record is not exists.")
    }

    private fun saveEnd(statement: Statement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing updated end period to database.", cause = exception)
    }

    override fun tryFindEndDateByCpidAndStage(cpid: Cpid, stage: Stage): Result<LocalDateTime?, Fail.Incident> {
        val statement = preparedFindEndDateByCpidAndStageCQL.bind()
            .apply {
                setString(columnCpid, cpid.toString())
                setString(columnStage, stage.toString())
            }
        return statement.tryExecute(session = session)
            .forwardResult { error -> return error }
            .one()
            ?.getTimestamp(columnEndDate)
            ?.toLocalDateTime()
            .asSuccess()
    }

    override fun trySaveEnd(cpid: Cpid, stage: Stage, endDate: LocalDateTime): Result<Boolean, Fail.Incident> {
        val statement = preparedSaveEndDateCQL.bind()
            .apply {
                setString(columnCpid, cpid.toString())
                setString(columnStage, stage.toString())
                setTimestamp(columnEndDate, endDate.toCassandraTimestamp())
            }
        statement.tryExecute(session = session)
            .doOnError { error -> return Result.failure(error) }

        return true.asSuccess()
    }
}
