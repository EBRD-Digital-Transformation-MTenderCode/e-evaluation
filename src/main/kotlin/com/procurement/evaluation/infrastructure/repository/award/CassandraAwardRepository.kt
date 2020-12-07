package com.procurement.evaluation.infrastructure.repository.award

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.procurement.evaluation.application.repository.award.AwardRepository
import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.tryOwner
import com.procurement.evaluation.infrastructure.extension.cassandra.tryExecute
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.repository.Database
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import org.springframework.stereotype.Repository

@Repository
class CassandraAwardRepository(private val session: Session) : AwardRepository {
    companion object {

        private const val FIND_BY_CPID_CQL = """
               SELECT ${Database.Awards.CPID},
                      ${Database.Awards.OCID},
                      ${Database.Awards.TOKEN_ENTITY},
                      ${Database.Awards.OWNER},
                      ${Database.Awards.STATUS},
                      ${Database.Awards.STATUS_DETAILS},
                      ${Database.Awards.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.Awards.TABLE_NAME}
                WHERE ${Database.Awards.CPID}=?
            """

        private const val FIND_BY_CPID_AND_OCID_CQL = """
               SELECT ${Database.Awards.CPID},
                      ${Database.Awards.OCID},
                      ${Database.Awards.TOKEN_ENTITY},
                      ${Database.Awards.OWNER},
                      ${Database.Awards.STATUS},
                      ${Database.Awards.STATUS_DETAILS},
                      ${Database.Awards.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.Awards.TABLE_NAME}
                WHERE ${Database.Awards.CPID}=?
                  AND ${Database.Awards.OCID}=?
            """

        private const val FIND_BY_CPID_AND_OCID_AND_TOKEN_CQL = """
               SELECT ${Database.Awards.CPID},
                      ${Database.Awards.OCID},
                      ${Database.Awards.TOKEN_ENTITY},
                      ${Database.Awards.OWNER},
                      ${Database.Awards.STATUS},
                      ${Database.Awards.STATUS_DETAILS},
                      ${Database.Awards.JSON_DATA}
                 FROM ${Database.KEYSPACE}.${Database.Awards.TABLE_NAME}
                WHERE ${Database.Awards.CPID}=?
                  AND ${Database.Awards.OCID}=?
                  AND ${Database.Awards.TOKEN_ENTITY}=?
            """

        private const val SAVE_NEW_AWARD_CQL = """
               INSERT INTO ${Database.KEYSPACE}.${Database.Awards.TABLE_NAME}(
                      ${Database.Awards.CPID},
                      ${Database.Awards.OCID},
                      ${Database.Awards.TOKEN_ENTITY},
                      ${Database.Awards.OWNER},
                      ${Database.Awards.STATUS},
                      ${Database.Awards.STATUS_DETAILS},
                      ${Database.Awards.JSON_DATA}
               )
               VALUES(?,?,?,?,?,?,?)
               IF NOT EXISTS
            """

        private const val UPDATE_AWARD_STATUSES_CQL = """
               UPDATE ${Database.KEYSPACE}.${Database.Awards.TABLE_NAME}
                  SET ${Database.Awards.STATUS}=?,
                      ${Database.Awards.STATUS_DETAILS}=?,
                      ${Database.Awards.JSON_DATA}=?
                WHERE ${Database.Awards.CPID}=?
                  AND ${Database.Awards.OCID}=?
                  AND ${Database.Awards.TOKEN_ENTITY}=?
               IF EXISTS
            """
    }

    private val preparedFindByCpidCQL = session.prepare(FIND_BY_CPID_CQL)
    private val preparedFindByCpidAndOcidCQL = session.prepare(FIND_BY_CPID_AND_OCID_CQL)
    private val preparedFindByCpidAndOcidAndTokenCQL = session.prepare(FIND_BY_CPID_AND_OCID_AND_TOKEN_CQL)
    private val preparedSaveNewAwardCQL = session.prepare(SAVE_NEW_AWARD_CQL)
    private val preparedUpdatedAwardStatusesCQL = session.prepare(UPDATE_AWARD_STATUSES_CQL)

    override fun findBy(cpid: Cpid): Result<List<AwardEntity>, Failure.Incident.Database> =
        preparedFindByCpidCQL.bind()
            .apply {
                setString(Database.Awards.CPID, cpid.underlying)
            }
            .tryExecute(session)
            .onFailure { return it }
            .map { convertToAwardEntity(it) }
            .asSuccess()

    override fun findBy(cpid: Cpid, ocid: Ocid, token: Token): Result<AwardEntity?, Failure.Incident.Database> =
        preparedFindByCpidAndOcidAndTokenCQL.bind()
            .apply {
                setString(Database.Awards.CPID, cpid.underlying)
                setString(Database.Awards.OCID, ocid.underlying)
                setString(Database.Awards.TOKEN_ENTITY, token.toString())
            }
            .tryExecute(session)
            .onFailure { return it }
            .one()
            ?.let { convertToAwardEntity(it) }
            .asSuccess()

    override fun save(cpid: Cpid, award: AwardEntity): Result<Boolean, Failure.Incident.Database> =
        statementForSaveAward(cpid, award)
            .tryExecute(session)
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun update(cpid: Cpid, updatedAwards: Collection<AwardEntity>): Result<Boolean, Failure.Incident.Database> =
        BatchStatement()
            .apply {
                for (updatedAward in updatedAwards) {
                    add(statementForUpdateAward(cpid = cpid, updatedAward = updatedAward))
                }
            }
            .tryExecute(session)
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun findBy(cpid: Cpid, ocid: Ocid): Result<List<AwardEntity>, Failure.Incident.Database> =
        preparedFindByCpidAndOcidCQL.bind()
            .apply {
                setString(Database.Awards.CPID, cpid.underlying)
                setString(Database.Awards.OCID, ocid.underlying)
            }
            .tryExecute(session)
            .onFailure { return it }
            .map { convertToAwardEntity(it) }
            .asSuccess()

    override fun save(cpid: Cpid, awards: Collection<AwardEntity>): Result<Boolean, Failure.Incident.Database> =
        BatchStatement()
            .apply {
                for (award in awards) {
                    add(statementForSaveAward(cpid = cpid, award = award))
                }
            }
            .tryExecute(session = session)
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    override fun update(cpid: Cpid, updatedAward: AwardEntity): Result<Boolean, Failure.Incident.Database> =
        statementForUpdateAward(cpid = cpid, updatedAward = updatedAward)
            .tryExecute(session = session)
            .onFailure { return it }
            .wasApplied()
            .asSuccess()

    private fun statementForUpdateAward(cpid: Cpid, updatedAward: AwardEntity): BoundStatement =
        preparedUpdatedAwardStatusesCQL.bind()
            .apply {
                setString(Database.Awards.CPID, cpid.underlying)
                setString(Database.Awards.OCID, updatedAward.ocid.underlying)
                setString(Database.Awards.TOKEN_ENTITY, updatedAward.token.toString())
                setString(Database.Awards.STATUS, updatedAward.status.toString())
                setString(Database.Awards.STATUS_DETAILS, updatedAward.statusDetails.toString())
                setString(Database.Awards.JSON_DATA, updatedAward.jsonData)
            }

    private fun statementForSaveAward(cpid: Cpid, award: AwardEntity): BoundStatement =
        preparedSaveNewAwardCQL.bind()
            .apply {
                setString(Database.Awards.CPID, cpid.underlying)
                setString(Database.Awards.OCID, award.ocid.underlying)
                setString(Database.Awards.TOKEN_ENTITY, award.token.toString())
                setString(Database.Awards.OWNER, award.owner?.toString())
                setString(Database.Awards.STATUS, award.status.toString())
                setString(Database.Awards.STATUS_DETAILS, award.statusDetails.toString())
                setString(Database.Awards.JSON_DATA, award.jsonData)
            }

    private fun convertToAwardEntity(row: Row): AwardEntity = AwardEntity(
        cpid = Cpid.tryCreateOrNull(row.getString(Database.Awards.CPID))!!,
        token = Token.fromString(row.getString(Database.Awards.TOKEN_ENTITY)),
        ocid = Ocid.tryCreateOrNull(row.getString(Database.Awards.OCID))!!,
        owner = row.getString(Database.Awards.OWNER)?.tryOwner()?.orNull,
        status = AwardStatus.creator(row.getString(Database.Awards.STATUS)),
        statusDetails = AwardStatusDetails.creator(row.getString(Database.Awards.STATUS_DETAILS)),
        jsonData = row.getString(Database.Awards.JSON_DATA)
    )
}
