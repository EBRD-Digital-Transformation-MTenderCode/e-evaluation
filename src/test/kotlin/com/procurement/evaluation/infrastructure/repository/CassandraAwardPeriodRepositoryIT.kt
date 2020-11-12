package com.procurement.evaluation.infrastructure.repository

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.evaluation.application.exception.ReadEntityException
import com.procurement.evaluation.application.exception.SaveEntityException
import com.procurement.evaluation.application.repository.period.AwardPeriodRepository
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.repository.period.CassandraAwardPeriodRepository
import com.procurement.evaluation.infrastructure.tools.toCassandraTimestamp
import com.procurement.evaluation.infrastructure.tools.toLocalDateTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraAwardPeriodRepositoryIT {
    companion object {
        private const val AWARD_CRITERIA = "awardCriteria"

        private val CPID = Cpid.tryCreateOrNull("ocds-t1s2t3-MD-1546004674286")!!
        private val OCID = Ocid.tryCreateOrNull("ocds-t1s2t3-MD-1546004674286-AC-1545606113365")!!
        private val START_DATE = LocalDateTime.now()
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var awardPeriodRepository: AwardPeriodRepository

    @BeforeEach
    fun init() {
        val poolingOptions = PoolingOptions()
            .setMaxConnectionsPerHost(HostDistance.LOCAL, 1)
        val cluster = Cluster.builder()
            .addContactPoints(container.contractPoint)
            .withPort(container.port)
            .withoutJMXReporting()
            .withPoolingOptions(poolingOptions)
            .withAuthProvider(PlainTextAuthProvider(container.username, container.password))
            .build()

        session = spy(cluster.connect())

        createKeyspace()
        createTable()

        awardPeriodRepository = CassandraAwardPeriodRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findBy() {
        insertAwardPeriod()

        val actualFundedAwardPeriodStartDate = awardPeriodRepository.findStartDateBy(cpid = CPID, ocid = OCID)

        assertNotNull(actualFundedAwardPeriodStartDate)
        assertEquals(START_DATE, actualFundedAwardPeriodStartDate)
    }

    @Test
    fun awardPeriodNotFound() {
        val UNKNOWN_CPID = Cpid.tryCreateOrNull("nope-t1s2t3-MD-1234564674286")!!
        val actualFundedAwardPeriodStartDate = awardPeriodRepository.findStartDateBy(cpid = UNKNOWN_CPID, ocid = OCID)
        assertNull(actualFundedAwardPeriodStartDate)
    }

    @Test
    fun errorRead() {

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val exception = assertThrows<ReadEntityException> {
            awardPeriodRepository.findStartDateBy(cpid = CPID, ocid = OCID)
        }
        assertEquals("Error read Award(s) from the database.", exception.message)
    }

    @Test
    fun saveNewStart() {
        awardPeriodRepository.saveNewStart(cpid = CPID, ocid = OCID, start = START_DATE)

        val actualFundedAwardPeriodStartDate = awardPeriodRepository.findStartDateBy(cpid = CPID, ocid = OCID)

        assertNotNull(actualFundedAwardPeriodStartDate)
        assertEquals(START_DATE, actualFundedAwardPeriodStartDate)
    }

    @Test
    fun errorAlreadyNewStart() {
        awardPeriodRepository.saveNewStart(cpid = CPID, ocid = OCID, start = START_DATE)

        val exception = assertThrows<SaveEntityException> {
            awardPeriodRepository.saveNewStart(cpid = CPID, ocid = OCID, start = START_DATE)
        }
        assertEquals(
            "An error occurred when writing a record(s) of the start award period '$START_DATE' by cpid '$CPID' and ocid '$OCID' to the database. Record is already.",
            exception.message
        )
    }

    @Test
    fun errorSaveNewStart() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val exception = assertThrows<SaveEntityException> {
            awardPeriodRepository.saveNewStart(cpid = CPID, ocid = OCID, start = START_DATE)
        }
        assertEquals("Error writing start date of the award period.", exception.message)
    }


    private fun createKeyspace() {
        session.execute("CREATE KEYSPACE ${Database.KEYSPACE} WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};")
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE ${Database.KEYSPACE};")
    }

    private fun createTable() {
        session.execute(
            """
                   CREATE TABLE IF NOT EXISTS ${Database.KEYSPACE}.${Database.Period.TABLE_NAME}
                    (
                        cpid           TEXT,
                        ocid           TEXT,
                        award_criteria TEXT,
                        start_date     TIMESTAMP,
                        end_date       TIMESTAMP,
                        PRIMARY KEY (cpid, ocid)
                    );
            """
        )
    }

    private fun insertAwardPeriod(endDate: LocalDateTime? = null) {
        val rec = QueryBuilder.insertInto(Database.KEYSPACE, Database.Period.TABLE_NAME)
            .value(Database.Period.CPID, CPID.underlying)
            .value(Database.Period.OCID, OCID.underlying)
            .value(Database.Period.AWARD_CRITERIA, AWARD_CRITERIA)
            .value(Database.Period.START_DATE, START_DATE.toCassandraTimestamp())
            .value(Database.Period.END_DATE, endDate?.toCassandraTimestamp())
        session.execute(rec)
    }

    private fun findAwardPeriodEnd(): LocalDateTime? {
        val query = QueryBuilder.select(Database.Period.END_DATE)
            .from(Database.KEYSPACE, Database.Period.TABLE_NAME)
            .where(QueryBuilder.eq(Database.Period.CPID, CPID.underlying))
            .and(QueryBuilder.eq(Database.Period.OCID, OCID.underlying))

        return session.execute(query)
            .one()
            ?.getTimestamp(Database.Period.END_DATE)
            ?.toLocalDateTime()
    }
}
