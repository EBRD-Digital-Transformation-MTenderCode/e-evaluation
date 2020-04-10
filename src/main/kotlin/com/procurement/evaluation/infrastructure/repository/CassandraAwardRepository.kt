package com.procurement.evaluation.infrastructure.repository

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.Statement
import com.procurement.evaluation.application.exception.ReadEntityException
import com.procurement.evaluation.application.exception.SaveEntityException
import com.procurement.evaluation.application.repository.AwardRepository
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.Result.Companion.failure
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.enums.Stage
import com.procurement.evaluation.infrastructure.extension.cassandra.tryExecute
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.utils.tryToObject
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
        val statement = statementForAwardSave(cpid, award)

        val result = saveNew(statement)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the award by cpid '$cpid' and stage '${award.stage}' to the database. Record is already.")
    }

    private fun statementForAwardSave(
        cpid: String,
        award: AwardEntity
    ): BoundStatement = preparedSaveNewAwardCQL.bind()
        .apply {
            setString(columnCpid, cpid)
            setString(columnStage, award.stage)
            setUUID(columnToken, award.token)
            setString(columnOwner, award.owner)
            setString(columnStatus, award.status)
            setString(columnStatusDetails, award.statusDetails)
            setString(columnJsonData, award.jsonData)
        }

    private fun saveNew(statement: BoundStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing new award to database.", cause = exception)
    }

    override fun saveNew(cpid: String, awards: List<AwardEntity>) {
        val statements = BatchStatement().apply {
            for (award in awards) {
                add(statementForAwardSave(cpid = cpid, award = award))
            }
        }
        val result = saveNewAwards(statements)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the award(s) by cpid '$cpid' to the database.")
    }

    private fun saveNewAwards(statement: BatchStatement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing new award(s) to database.", cause = exception)
    }

    override fun update(cpid: String, updatedAward: AwardEntity) {
        val statement = statementForUpdateAward(cpid = cpid, updatedAward = updatedAward)
        val result = executeUpdating(statement)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the award by cpid '$cpid' and stage '${updatedAward.stage}' and token to the database. Record is already.")
    }

    override fun update(cpid: String, updatedAwards: Collection<AwardEntity>) {
        val statements = BatchStatement().apply {
            for (updatedAward in updatedAwards) {
                add(statementForUpdateAward(cpid = cpid, updatedAward = updatedAward))
            }
        }
        val result = executeUpdating(statements)
        if (!result.wasApplied())
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the awards by cpid '$cpid' to the database. Record(s) is not exists.")
    }

    override fun tryFindBy(cpid: Cpid, stage: Stage): Result<List<AwardEntity>, Fail.Incident> {
        val query = preparedFindByCpidAndStageCQL.bind()
            .apply {
                setString(columnCpid, cpid.toString())
                setString(columnStage, stage.toString())
            }

        val resultSet = query.tryExecute(session)
            .doReturn { error -> return failure(error) }
        return resultSet.map { convertToAwardEntity(it) }.asSuccess()
    }

    override fun tryFindBy(cpid: Cpid, stage: Stage, awardId: AwardId): Result<AwardEntity?, Fail> {
        val awardEntities = tryFindBy(
            cpid = cpid, stage = stage
        )
            .forwardResult { incident -> return incident }
            .takeIf { it.isNotEmpty() }
            ?: return null.asSuccess()

        for (entity in awardEntities) {
            val award = entity.jsonData
                .tryToObject(Award::class.java)
                .doReturn { error ->
                    return failure(Fail.Incident.Transform.ParseFromDatabaseIncident(entity.jsonData, error.exception))
                }
            if (award.id == awardId.toString())
                return entity.asSuccess()
        }
        return null.asSuccess()
    }

    override fun trySave(cpid: Cpid, awards: List<AwardEntity>): Result<Unit, Fail.Incident> {
        val statements = BatchStatement()
            .apply {
                for (award in awards) {
                    add(statementForAwardSave(cpid = cpid.toString(), award = award))
                }
            }
        val result = statements.tryExecute(session = session)
            .forwardResult { error -> return error }

        if (!result.wasApplied())
            return failure(
                Fail.Incident.Database.RecordIsNotExist(description = "An error occurred when writing a record(s) of the awards by cpid '$cpid' to the database. Record(s) is not exists.")
            )

        return Unit.asSuccess()
    }

    private fun statementForUpdateAward(cpid: String, updatedAward: AwardEntity): Statement =
        preparedUpdatedAwardStatusesCQL.bind()
            .apply {
                setString(columnCpid, cpid)
                setString(columnStage, updatedAward.stage)
                setUUID(columnToken, updatedAward.token)
                setString(columnStatus, updatedAward.status)
                setString(columnStatusDetails, updatedAward.statusDetails)
                setString(columnJsonData, updatedAward.jsonData)
            }

    private fun executeUpdating(statement: Statement): ResultSet = try {
        session.execute(statement)
    } catch (exception: Exception) {
        throw SaveEntityException(message = "Error writing updated award to database.", cause = exception)
    }
}
