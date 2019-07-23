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
import com.procurement.evaluation.application.repository.AwardRepository
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.entity.AwardEntity
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [DatabaseTestConfiguration::class])
class CassandraAwardRepositoryIT {
    companion object {
        private const val CPID = "cpid-1"
        private const val STAGE = "EV"
        private val TOKEN = UUID.randomUUID()
        private const val OWNER = "owner-1"
        private val AWARD_STATUS = AwardStatus.PENDING
        private val AWARD_STATUS_DETAILS = AwardStatusDetails.EMPTY
        private const val JSON_DATA = """ {"award": "data"} """
    }

    @Autowired
    private lateinit var container: CassandraTestContainer

    private lateinit var session: Session
    private lateinit var awardRepository: AwardRepository

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

        awardRepository = CassandraAwardRepository(session)
    }

    @AfterEach
    fun clean() {
        dropKeyspace()
    }

    @Test
    fun findBy() {
        insertAward()

        val actualFundedAwards = awardRepository.findBy(cpid = CPID)

        assertEquals(1, actualFundedAwards.size)
        assertEquals(expectedFundedAward(), actualFundedAwards[0])
    }

    @Test
    fun awardNotFound() {
        val actualFundedAwards = awardRepository.findBy(cpid = "UNKNOWN")
        assertEquals(0, actualFundedAwards.size)
    }

    @Test
    fun errorRead() {
        insertAward()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val exception = assertThrows<ReadEntityException> {
            awardRepository.findBy(cpid = CPID)
        }
        assertEquals("Error read Award(s) from the database.", exception.message)
    }

    @Test
    fun saveNewAward() {
        val awardEntity = AwardEntity(
            cpId = CPID,
            stage = STAGE,
            token = TOKEN,
            status = AWARD_STATUS.value,
            statusDetails = AWARD_STATUS_DETAILS.value,
            owner = OWNER,
            jsonData = JSON_DATA
        )
        awardRepository.saveNew(cpid = CPID, award = awardEntity)

        val actualFundedAwards = awardRepository.findBy(cpid = CPID)

        assertEquals(1, actualFundedAwards.size)
        assertEquals(expectedFundedAward(), actualFundedAwards[0])
    }

    @Test
    fun errorAlreadyAward() {
        val awardEntity = AwardEntity(
            cpId = CPID,
            stage = STAGE,
            token = TOKEN,
            status = AWARD_STATUS.value,
            statusDetails = AWARD_STATUS_DETAILS.value,
            owner = OWNER,
            jsonData = JSON_DATA
        )
        awardRepository.saveNew(cpid = CPID, award = awardEntity)

        val exception = assertThrows<SaveEntityException> {
            awardRepository.saveNew(cpid = CPID, award = awardEntity)
        }

        assertEquals(
            "An error occurred when writing a record(s) of the award by cpid '$CPID' and stage '$STAGE' to the database. Record is already.",
            exception.message
        )
    }

    @Test
    fun errorSaveNewStart() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val awardEntity = AwardEntity(
            cpId = CPID,
            stage = STAGE,
            token = TOKEN,
            status = AWARD_STATUS.value,
            statusDetails = AWARD_STATUS_DETAILS.value,
            owner = OWNER,
            jsonData = JSON_DATA
        )

        val exception = assertThrows<SaveEntityException> {
            awardRepository.saveNew(cpid = CPID, award = awardEntity)
        }
        assertEquals("Error writing new award to database.", exception.message)
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
                CREATE TABLE IF NOT EXISTS ocds.evaluation_award (
                    cp_id text,
                    stage text,
                    token_entity UUID,
                    owner text,
                    status text,
                    status_details text,
                    json_data text,
                    PRIMARY KEY(cp_id, stage, token_entity)
                );
            """
        )
    }

    private fun expectedFundedAward() = AwardEntity(
        cpId = CPID,
        stage = STAGE,
        token = TOKEN,
        owner = OWNER,
        status = AWARD_STATUS.toString(),
        statusDetails = AWARD_STATUS_DETAILS.toString(),
        jsonData = JSON_DATA
    )

    private fun insertAward(
        status: AwardStatus = AWARD_STATUS,
        statusDetails: AwardStatusDetails = AWARD_STATUS_DETAILS,
        jsonData: String = JSON_DATA
    ) {
        val rec = QueryBuilder.insertInto("ocds", "evaluation_award")
            .value("cp_id", CPID)
            .value("stage", STAGE)
            .value("token_entity", TOKEN)
            .value("owner", OWNER)
            .value("status", status.toString())
            .value("status_details", statusDetails.toString())
            .value("json_data", jsonData)
        session.execute(rec)
    }
}
