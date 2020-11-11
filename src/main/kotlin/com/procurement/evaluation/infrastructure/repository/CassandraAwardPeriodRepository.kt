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
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.extension.cassandra.tryExecute
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.tools.toCassandraTimestamp
import com.procurement.evaluation.infrastructure.tools.toLocalDateTime
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CassandraAwardPeriodRepository(private val session: Session) : AwardPeriodRepository {
    companion object {

        private const val FIND_BY_CPID_AND_STAGE_CQL = """
               SELECT ${Database.Period.START_DATE}
                 FROM ${Database.KEYSPACE}.${Database.Period.TABLE_NAME}
                WHERE ${Database.Period.CPID}=?
                  AND ${Database.Period.OCID}=?
                  LIMIT 1
            """

        private const val FIND_START_DATE_BY_CPID_AND_STAGE_CQL = """
            SELECT ${Database.Period.START_DATE}
                 FROM ${Database.KEYSPACE}.${Database.Period.TABLE_NAME}
                WHERE ${Database.Period.CPID}=?
                  AND ${Database.Period.OCID}=?
        """

        private const val SAVE_NEW_START_DATE_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.Period.TABLE_NAME}(
                           ${Database.Period.CPID}, 
                           ${Database.Period.OCID}, 
                           ${Database.Period.START_DATE}
               )
               VALUES(?, ?, ?) 
               IF NOT EXISTS
            """

        private const val SAVE_END_DATE_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.Period.TABLE_NAME}
                  SET ${Database.Period.END_DATE}=?
                WHERE ${Database.Period.CPID}=?
                  AND ${Database.Period.OCID}=?
               IF EXISTS
            """
    }

    private val preparedFindByCpidAndStageCQL = session.prepare(FIND_BY_CPID_AND_STAGE_CQL)
    private val preparedSaveNewStartDateCQL = session.prepare(SAVE_NEW_START_DATE_CQL)
    private val preparedSaveEndDateCQL = session.prepare(SAVE_END_DATE_CQL)
    private val preparedFindStartDateByCpidAndStageCQL = session.prepare(FIND_START_DATE_BY_CPID_AND_STAGE_CQL)

    override fun findStartDateBy(cpid: Cpid, ocid: Ocid): LocalDateTime? {
        val query = preparedFindByCpidAndStageCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.toString())
                setString(Database.Period.OCID, ocid.toString())
            }

        val resultSet = load(query)
        return resultSet.one()?.getTimestamp(Database.Period.START_DATE)?.toLocalDateTime()
    }

    protected fun load(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw ReadEntityException(message = "Error read Award(s) from the database.", cause = exception)
    }

    override fun saveNewStart(cpid: Cpid, ocid: Ocid, start: LocalDateTime) {
        val statement = preparedSaveNewStartDateCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.toString())
                setString(Database.Period.OCID, ocid.toString())
                setTimestamp(Database.Period.START_DATE, start.toCassandraTimestamp())
            }

        val result = saveNewStart(statement)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the start award period '$start' by cpid '$cpid' and ocid '$ocid' to the database. Record is already.")
    }

    private fun saveNewStart(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing start date of the award period.", cause = exception)
    }

    override fun saveEnd(cpid: Cpid, ocid: Ocid, end: LocalDateTime) {
        val statement = preparedSaveEndDateCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.toString())
                setString(Database.Period.OCID, ocid.toString())
                setTimestamp(Database.Period.END_DATE, end.toCassandraTimestamp())
            }

        val result = saveEnd(statement)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the end award period '$end' by cpid '$cpid' and ocid '$ocid' to the database. Record is not exists.")
    }

    private fun saveEnd(statement: Statement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing updated end period to database.", cause = exception)
    }

    override fun tryFindStartDateByCpidAndStage(cpid: Cpid, ocid: Ocid): Result<LocalDateTime?, Fail.Incident> {
        val statement = preparedFindStartDateByCpidAndStageCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.toString())
                setString(Database.Period.OCID, ocid.toString())
            }
        return statement.tryExecute(session = session)
            .orForwardFail { error -> return error }
            .one()
            ?.getTimestamp(Database.Period.START_DATE)
            ?.toLocalDateTime()
            .asSuccess()
    }

    override fun trySaveEnd(cpid: Cpid, ocid: Ocid, endDate: LocalDateTime): Result<Unit, Fail.Incident> {
        val statement = preparedSaveEndDateCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.toString())
                setString(Database.Period.OCID, ocid.toString())
                setTimestamp(Database.Period.END_DATE, endDate.toCassandraTimestamp())
            }
        val result = statement.tryExecute(session = session)
            .orForwardFail { error -> return error }

        if (!result.wasApplied())
            return Result.failure(
                Fail.Incident.Database.RecordIsNotExist(description = "An error occurred when writing a record(s) of the end award period '$endDate' by cpid '$cpid' and stage '$ocid' to the database. Record is not exists.")
            )

        return Unit.asSuccess()
    }
}
