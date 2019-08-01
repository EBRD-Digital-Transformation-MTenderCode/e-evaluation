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
import java.util.*

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

        private const val FIND_BY_CPID_AND_STAGE_CQL = """
               SELECT $columnCpid,
                      $columnStage,
                      $columnToken,
                      $columnOwner,
                      $columnStatus,
                      $columnStatusDetails,
                      $columnJsonData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
                  AND $columnStage=?
            """

        private const val FIND_BY_CPID_AND_STAGE_AND_TOKEN_CQL = """
               SELECT $columnCpid,
                      $columnStage,
                      $columnToken,
                      $columnOwner,
                      $columnStatus,
                      $columnStatusDetails,
                      $columnJsonData
                 FROM $keySpace.$tableName
                WHERE $columnCpid=?
                  AND $columnStage=?
                  AND $columnToken=?
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

        private const val UPDATE_AWARD_STATUSES_CQL = """
               UPDATE $keySpace.$tableName
                  SET $columnStatus=?,
                      $columnStatusDetails=?,
                      $columnJsonData=?
                WHERE $columnCpid=?
                  AND $columnStage=?
                  AND $columnToken=?
               IF EXISTS
            """
    }

    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)
    private val preparedFindByCpidAndStageCQL = session.prepare(FIND_BY_CPID_AND_STAGE_CQL)
    private val preparedFindByCpidAndStageAndTokenCQL = session.prepare(FIND_BY_CPID_AND_STAGE_AND_TOKEN_CQL)
    private val preparedSaveNewAwardCQL = session.prepare(SAVE_NEW_AWARD_CQL)
    private val preparedUpdatedAwardStatusesCQL = session.prepare(UPDATE_AWARD_STATUSES_CQL)

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

    override fun findBy(cpid: String, stage: String): List<AwardEntity> {
        val query = preparedFindByCpidAndStageCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnStage, stage)
            }

        val resultSet = load(query)
        return resultSet.map { convertToAwardEntity(it) }
    }

    override fun findBy(cpid: String, stage: String, token: UUID): AwardEntity? {
        val query = preparedFindByCpidAndStageAndTokenCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnStage, stage)
                setUUID(columnToken, token)
            }

        val resultSet = load(query)
        return resultSet.one()?.let { convertToAwardEntity(it) }
    }

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

    override fun update(cpid: String, updatedAward: AwardEntity) {
        val statement = preparedUpdatedAwardStatusesCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnStage, updatedAward.stage)
                setUUID(columnToken, updatedAward.token)
                setString(columnStatus, updatedAward.status)
                setString(columnStatusDetails, updatedAward.statusDetails)
                setString(columnJsonData, updatedAward.jsonData)
            }

        val result = update(statement)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the award by cpid '$cpid' and stage '${updatedAward.stage}' and token to the database. Record is already.")
    }

    private fun update(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing updated award to database.", cause = exception)
    }
}
