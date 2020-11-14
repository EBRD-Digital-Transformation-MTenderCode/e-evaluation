package com.procurement.evaluation.infrastructure.repository.period

import com.datastax.driver.core.Session
import com.procurement.evaluation.application.repository.period.AwardPeriodRepository
import com.procurement.evaluation.application.repository.period.model.PeriodEntity
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.extension.cassandra.toCassandraTimestamp
import com.procurement.evaluation.infrastructure.extension.cassandra.toLocalDateTime
import com.procurement.evaluation.infrastructure.extension.cassandra.tryExecute
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.repository.Database
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class CassandraAwardPeriodRepository(private val session: Session) : AwardPeriodRepository {
    companion object {

        private const val FIND_PERIOD_BY_CPID_CQL = """
               SELECT ${Database.Period.OCID},
                      ${Database.Period.START_DATE},
                      ${Database.Period.END_DATE}
                 FROM ${Database.KEYSPACE}.${Database.Period.TABLE_NAME}
                WHERE ${Database.Period.CPID}=?
            """

        private const val FIND_PERIOD_BY_CPID_AND_OCID_CQL = """
               SELECT ${Database.Period.START_DATE},
                      ${Database.Period.END_DATE}
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

    private val preparedFindPeriodByCpidCQL = session.prepare(FIND_PERIOD_BY_CPID_CQL)
    private val preparedFindPeriodByCpidAndOcidCQL = session.prepare(FIND_PERIOD_BY_CPID_AND_OCID_CQL)
    private val preparedSaveNewStartDateCQL = session.prepare(SAVE_NEW_START_DATE_CQL)
    private val preparedSaveEndDateCQL = session.prepare(SAVE_END_DATE_CQL)

    override fun findBy(cpid: Cpid): Result<List<PeriodEntity>, Fail.Incident.Database> =
        preparedFindPeriodByCpidCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.underlying)
            }
            .tryExecute(session = session)
            .orForwardFail { error -> return error }
            .map { row ->
                PeriodEntity(
                    cpid = cpid,
                    ocid = Ocid.tryCreateOrNull(row.getString(Database.Period.OCID))!!,
                    startDate = row.getTimestamp(Database.Period.START_DATE).toLocalDateTime(),
                    endDate = row.getTimestamp(Database.Period.END_DATE)?.toLocalDateTime()
                )
            }
            .asSuccess()

    override fun findBy(cpid: Cpid, ocid: Ocid): Result<PeriodEntity?, Fail.Incident.Database> =
        preparedFindPeriodByCpidAndOcidCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.underlying)
                setString(Database.Period.OCID, ocid.underlying)
            }
            .tryExecute(session = session)
            .orForwardFail { error -> return error }
            .one()
            ?.let { row ->
                PeriodEntity(
                    cpid = cpid,
                    ocid = ocid,
                    startDate = row.getTimestamp(Database.Period.START_DATE).toLocalDateTime(),
                    endDate = row.getTimestamp(Database.Period.END_DATE)?.toLocalDateTime()
                )
            }
            .asSuccess()

    override fun saveStart(cpid: Cpid, ocid: Ocid, start: LocalDateTime): Result<Boolean, Fail.Incident.Database> =
        preparedSaveNewStartDateCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.underlying)
                setString(Database.Period.OCID, ocid.underlying)
                setTimestamp(Database.Period.START_DATE, start.toCassandraTimestamp())
            }
            .tryExecute(session)
            .orForwardFail { return it }
            .wasApplied()
            .asSuccess()

    override fun saveEnd(cpid: Cpid, ocid: Ocid, endDate: LocalDateTime): Result<Boolean, Fail.Incident.Database> =
        preparedSaveEndDateCQL.bind()
            .apply {
                setString(Database.Period.CPID, cpid.underlying)
                setString(Database.Period.OCID, ocid.underlying)
                setTimestamp(Database.Period.END_DATE, endDate.toCassandraTimestamp())
            }
            .tryExecute(session = session)
            .orForwardFail { error -> return error }
            .wasApplied()
            .asSuccess()
}
