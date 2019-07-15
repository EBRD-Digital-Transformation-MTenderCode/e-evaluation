package com.procurement.evaluation.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.evaluation.application.exception.ReadEntityException
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
    }

    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)

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

    }
}
