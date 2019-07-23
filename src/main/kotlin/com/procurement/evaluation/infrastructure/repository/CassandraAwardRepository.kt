package com.procurement.evaluation.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.evaluation.application.exception.ReadEntityException
import com.procurement.evaluation.application.exception.SaveEntityException
import com.procurement.evaluation.application.repository.AwardRepository
import com.procurement.evaluation.model.entity.AwardEntity
import org.springframework.stereotype.Repository

@Repository
class CassandraAwardRepository(private val session: Session) : AwardRepository {
    companion object {
        private const val keySpace = "ocds"
        private const val tableName = "evaluation_award"
        private const val columnCpid = "cp_id"
        private const val columnStage = "stage"
        private const val columnToken = "token_entity"
        private const val columnOwner = "owner"
        private const val columnStatus = "status"
        private const val columnStatusDetails = "status_details"
        private const val columnJsonData = "json_data"

        /*
        companion object {
        private const val AWARD_TABLE = "evaluation_award"
        private const val CP_ID = "cp_id"
        private const val STAGE = "stage"
        private const val TOKEN = "token_entity"
        private const val OWNER = "owner"
        private const val STATUS = "status"
        private const val STATUS_DETAILS = "status_details"
        private const val JSON_DATA = "json_data"
    }
         */
        private const val FIND_BY_CPID_CQL = """
               SELECT $columnCpid,
                      $columnStage,
                      $columnToken,
                      $columnOwner,
                      $columnStatus,
                      $columnStatusDetails,
                      $columnJsonData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
            """

        private const val SAVE_NEW_AWARD_CQL = """
               INSERT INTO $keySpace.$tableName(
                      $columnCpid,
                      $columnStage,
                      $columnToken,
                      $columnOwner,
                      $columnStatus,
                      $columnStatusDetails,
                      $columnJsonData
               )
               VALUES(?,?,?,?,?,?,?)
               IF NOT EXISTS
            """
    }

    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)
    private val preparedSaveNewAwardCQL = session.prepare(SAVE_NEW_AWARD_CQL)

    override fun findBy(cpid: String): List<AwardEntity> {
        val query = preparedFindByCpidCQL.bind()
            .apply {
                setString(columnCpid, cpid)
            }

        val resultSet = load(query)
        return resultSet.map { convertToAwardEntity(it) }
    }

    protected fun load(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw ReadEntityException(message = "Error read Award(s) from the database.", cause = exception)
    }

    private fun convertToAwardEntity(row: Row): AwardEntity = AwardEntity(
        cpId = row.getString(columnCpid),
        token = row.getUUID(columnToken),
        stage = row.getString(columnStage),
        owner = row.getString(columnOwner),
        status = row.getString(columnStatus),
        statusDetails = row.getString(columnStatusDetails),
        jsonData = row.getString(columnJsonData)
    )

    override fun saveNew(cpid: String, award: AwardEntity) {
        val statement = preparedSaveNewAwardCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnStage, award.stage)
                setUUID(columnToken, award.token)
                setString(columnOwner, award.owner)
                setString(columnStatus, award.status)
                setString(columnStatusDetails, award.statusDetails)
                setString(columnJsonData, award.jsonData)
            }

        val result = saveNew(statement)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the award by cpid '$cpid' and stage '${award.stage}' to the database. Record is already.")
    }

    private fun saveNew(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing new award to database.", cause = exception)
    }
}
