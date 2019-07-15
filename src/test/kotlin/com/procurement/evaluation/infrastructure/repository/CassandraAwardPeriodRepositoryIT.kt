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
import com.procurement.evaluation.application.repository.AwardPeriodRepository
import com.procurement.evaluation.infrastructure.tools.toCassandraTimestamp
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
        private const val CPID = "cpid-1"
        private const val STAGE = "EV"
        private const val AWARD_CRITERIA = "awardCriteria"
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

        val actualFundedAwardPeriodStartDate = awardPeriodRepository.findStartDateBy(cpid = CPID, stage = STAGE)

        assertNotNull(actualFundedAwardPeriodStartDate)
        assertEquals(START_DATE, actualFundedAwardPeriodStartDate)
    }

    @Test
    fun awardPeriodNotFound() {
        val actualFundedAwardPeriodStartDate = awardPeriodRepository.findStartDateBy(cpid = "UNKNOWN", stage = STAGE)
        assertNull(actualFundedAwardPeriodStartDate)
    }

    @Test
    fun errorRead() {

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val exception = assertThrows<ReadEntityException> {
            awardPeriodRepository.findStartDateBy(cpid = CPID, stage = STAGE)
        }
        assertEquals("Error read Award(s) from the database.", exception.message)
    }

    @Test
    fun saveNewStart() {
        awardPeriodRepository.saveNewStart(cpid = CPID, stage = STAGE, start = START_DATE)

        val actualFundedAwardPeriodStartDate = awardPeriodRepository.findStartDateBy(cpid = CPID, stage = STAGE)

        assertNotNull(actualFundedAwardPeriodStartDate)
        assertEquals(START_DATE, actualFundedAwardPeriodStartDate)
    }

    @Test
    fun errorSaveNewStart() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val exception = assertThrows<SaveEntityException> {
            awardPeriodRepository.saveNewStart(cpid = CPID, stage = STAGE, start = START_DATE)
        }
        assertEquals("Error writing start date of the award period.", exception.message)
    }

    private fun createKeyspace() {
        session.execute("CREATE KEYSPACE ocds WITH replication = {'class' : 'SimpleStrategy', 'replication_factor' : 1};")
    }

    private fun dropKeyspace() {
        session.execute("DROP KEYSPACE ocds;")
    }

    private fun createTable() {
        session.execute(
            """
                CREATE TABLE IF NOT EXISTS ocds.evaluation_period (
                    cp_id text,
                    stage text,
                    token_entity UUID,
                    award_criteria text,
                    start_date timestamp,
                    end_date timestamp,
                    PRIMARY KEY(cp_id, stage)
                );
            """
        )
    }

    private fun insertAwardPeriod(endDate: LocalDateTime? = null) {
        val rec = QueryBuilder.insertInto("ocds", "evaluation_period")
            .value("cp_id", CPID)
            .value("stage", STAGE)
            .value("award_criteria", AWARD_CRITERIA)
            .value("start_date", START_DATE.toCassandraTimestamp())
            .value("end_date", endDate?.toCassandraTimestamp())
        session.execute(rec)
    }
}
