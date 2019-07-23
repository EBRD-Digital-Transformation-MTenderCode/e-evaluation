package com.procurement.evaluation.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Session
import com.procurement.evaluation.application.exception.ReadEntityException
import com.procurement.evaluation.application.exception.SaveEntityException
import com.procurement.evaluation.application.repository.AwardPeriodRepository
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

        private const val FIND_BY_CPID_AND_STAGE_CQL = """
               SELECT $columnStartDate
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
                  AND $columnStage=?
                  LIMIT 1
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
    }

    private val preparedFindByCpidAndStageCQL = session.prepare(FIND_BY_CPID_AND_STAGE_CQL)
    private val preparedSaveNewStartDateCQL = session.prepare(SAVE_NEW_START_DATE_CQL)

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
}
