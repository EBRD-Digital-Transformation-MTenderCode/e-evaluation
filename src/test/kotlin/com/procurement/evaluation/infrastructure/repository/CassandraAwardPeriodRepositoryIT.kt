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
import com.procurement.evaluation.application.repository.period.AwardPeriodRepository
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.repository.period.CassandraAwardPeriodRepository
import com.procurement.evaluation.infrastructure.tools.toCassandraTimestamp
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraAwardPeriodRepositoryIT {
    companion object {

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

        val periodEntity = awardPeriodRepository.findBy(cpid = CPID, ocid = OCID).get
        assertNotNull(periodEntity)

        val actualFundedAwardPeriodStartDate = periodEntity!!.startDate
        assertEquals(START_DATE, actualFundedAwardPeriodStartDate)
    }

    @Test
    fun awardPeriodNotFound() {
        val UNKNOWN_CPID = Cpid.tryCreateOrNull("nope-t1s2t3-MD-1234564674286")!!
        val periodEntity = awardPeriodRepository.findBy(cpid = UNKNOWN_CPID, ocid = OCID).get
        assertNull(periodEntity)
    }

    @Test
    fun errorRead() {

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val result = awardPeriodRepository.findBy(cpid = CPID, ocid = OCID)

        assertTrue(result.isFail)
        assertTrue(result.error.exception is RuntimeException)
    }

    @Test
    fun saveNewStart() {
        awardPeriodRepository.saveStart(cpid = CPID, ocid = OCID, start = START_DATE)

        val periodEntity = awardPeriodRepository.findBy(cpid = CPID, ocid = OCID).get
        val actualFundedAwardPeriodStartDate = periodEntity?.startDate

        assertNotNull(periodEntity)
        assertEquals(START_DATE, actualFundedAwardPeriodStartDate)
    }

    @Test
    fun errorAlreadyNewStart() {
        awardPeriodRepository.saveStart(cpid = CPID, ocid = OCID, start = START_DATE)

        val result = awardPeriodRepository.saveStart(cpid = CPID, ocid = OCID, start = START_DATE)

        assertTrue(result.isSuccess)
        val wasApplied = result.get
        assertFalse(wasApplied)
    }

    @Test
    fun errorSaveNewStart() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val result = awardPeriodRepository.saveStart(cpid = CPID, ocid = OCID, start = START_DATE)

        assertTrue(result.isFail)
        assertTrue(result.error.exception is RuntimeException)

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
            .value(Database.Period.START_DATE, START_DATE.toCassandraTimestamp())
            .value(Database.Period.END_DATE, endDate?.toCassandraTimestamp())
        session.execute(rec)
    }
}
