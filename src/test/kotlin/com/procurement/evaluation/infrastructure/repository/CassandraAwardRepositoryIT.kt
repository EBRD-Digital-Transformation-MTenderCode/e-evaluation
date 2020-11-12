package com.procurement.evaluation.infrastructure.repository

import com.datastax.driver.core.BatchStatement
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
import com.procurement.evaluation.application.exception.SaveEntityException
import com.procurement.evaluation.application.repository.award.AwardRepository
import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.infrastructure.repository.award.CassandraAwardRepository
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
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
        private val CPID = Cpid.tryCreateOrNull("ocds-t1s2t3-MD-1546004674286")!!
        private val OCID = Ocid.tryCreateOrNull("ocds-t1s2t3-MD-1546004674286-AC-1545606113365")!!

        private val TOKEN: UUID = UUID.randomUUID()
        private val OWNER = Owner.fromString("9bd47f45-617f-4171-8673-80f40ced0774")
        private val AWARD_STATUS = AwardStatus.PENDING
        private val UPDATED_AWARD_STATUS = AwardStatus.ACTIVE
        private val AWARD_STATUS_DETAILS = AwardStatusDetails.EMPTY
        private val UPDATED_AWARD_STATUS_DETAILS = AwardStatusDetails.UNSUCCESSFUL
        private const val JSON_DATA = """ {"award": "data"} """
        private const val UPDATED_JSON_DATA = """ {"award": "updated data"} """
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
    fun findByCPID() {
        insertAward()

        val actualFundedAwards = awardRepository.findBy(cpid = CPID).orThrow { it.exception }

        assertEquals(1, actualFundedAwards.size)
        assertEquals(expectedFundedAward(), actualFundedAwards[0])
    }

    @Test
    fun awardByCPIDNotFound() {
        val actualFundedAwards = awardRepository.findBy(cpid = CPID).orThrow { it.exception }
        assertEquals(0, actualFundedAwards.size)
    }

    @Test
    fun errorReadByCPID() {
        insertAward()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val result = awardRepository.findBy(cpid = CPID)
        assertTrue(result.isFail)
        assertTrue(result.error.exception is RuntimeException)
    }

    @Test
    fun findByCPIDAndStage() {
        insertAward()

        val actualFundedAwards = awardRepository.findBy(cpid = CPID, ocid = OCID)
            .orThrow { it.exception }

        assertEquals(1, actualFundedAwards.size)
        assertEquals(expectedFundedAward(), actualFundedAwards[0])
    }

    @Test
    fun awardByCPIDAndStageNotFound() {
        val actualFundedAwards = awardRepository.findBy(cpid = CPID, ocid = OCID)
            .orThrow { it.exception }

        assertEquals(0, actualFundedAwards.size)
    }

    @Test
    fun errorReadByCPIDAndOcid() {
        insertAward()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val result = awardRepository.findBy(cpid = CPID, ocid = OCID)
        assertTrue(result.isFail)
        assertTrue(result.error.exception is RuntimeException)
    }

    @Test
    fun findByCPIDAndOcidAndToken() {
        insertAward()

        val actualFundedAward = awardRepository.findBy(cpid = CPID, ocid = OCID, token = TOKEN)
            .orThrow { it.exception }

        assertNotNull(actualFundedAward)
        assertEquals(expectedFundedAward(), actualFundedAward)
    }

    @Test
    fun awardByCPIDAndStageAndTokenNotFound() {
        val actualFundedAward = awardRepository.findBy(cpid = CPID, ocid = OCID, token = TOKEN)
            .orThrow { it.exception }

        assertNull(actualFundedAward)
    }

    @Test
    fun errorReadByCPIDAndStageAndToken() {
        insertAward()

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val result = awardRepository.findBy(cpid = CPID, ocid = OCID, token = TOKEN)

        assertTrue(result.isFail)
        assertTrue(result.error.exception is RuntimeException)
    }

    @Test
    fun saveNewAward() {
        val awardEntity = AwardEntity(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            status = AWARD_STATUS,
            statusDetails = AWARD_STATUS_DETAILS,
            owner = OWNER,
            jsonData = JSON_DATA
        )
        awardRepository.saveNew(cpid = CPID, award = awardEntity)

        val actualFundedAwards = awardRepository.findBy(cpid = CPID).orThrow { it.exception }

        assertEquals(1, actualFundedAwards.size)
        assertEquals(expectedFundedAward(), actualFundedAwards[0])
    }

    @Test
    fun errorAlreadyAward() {
        val awardEntity = AwardEntity(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            status = AWARD_STATUS,
            statusDetails = AWARD_STATUS_DETAILS,
            owner = OWNER,
            jsonData = JSON_DATA
        )
        awardRepository.saveNew(cpid = CPID, award = awardEntity)

        val exception = assertThrows<SaveEntityException> {
            awardRepository.saveNew(cpid = CPID, award = awardEntity)
        }

        assertEquals(
            "An error occurred when writing a record(s) of the award by cpid '$CPID' and ocid '$OCID' to the database. Record is already.",
            exception.message
        )
    }

    @Test
    fun errorSaveNewStart() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val awardEntity = AwardEntity(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            status = AWARD_STATUS,
            statusDetails = AWARD_STATUS_DETAILS,
            owner = OWNER,
            jsonData = JSON_DATA
        )

        val exception = assertThrows<SaveEntityException> {
            awardRepository.saveNew(cpid = CPID, award = awardEntity)
        }
        assertEquals("Error writing new award to database.", exception.message)
    }

    @Test
    fun update() {
        insertAward()

        val updatedAwardEntity = AwardEntity(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = UPDATED_AWARD_STATUS,
            statusDetails = UPDATED_AWARD_STATUS_DETAILS,
            jsonData = UPDATED_JSON_DATA
        )
        awardRepository.update(cpid = CPID, updatedAward = updatedAwardEntity)

        val actualFundedAward = awardRepository.findBy(cpid = CPID, ocid = OCID, token = TOKEN)
            .orThrow { it.exception }

        assertNotNull(actualFundedAward)
        assertEquals(updatedAwardEntity, actualFundedAward)
    }

    @Test
    fun recordForUpdateNotFound() {
        val updatedAwardEntity = AwardEntity(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = UPDATED_AWARD_STATUS,
            statusDetails = UPDATED_AWARD_STATUS_DETAILS,
            jsonData = UPDATED_JSON_DATA
        )
        val exception = assertThrows<SaveEntityException> {
            awardRepository.update(cpid = CPID, updatedAward = updatedAwardEntity)
        }

        assertEquals(
            "An error occurred when writing a record(s) of the award by cpid '$CPID' and ocid '$OCID' and token to the database. Record is already.",
            exception.message
        )
    }

    @Test
    fun errorUpdate() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val updatedAwardEntity = AwardEntity(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = UPDATED_AWARD_STATUS,
            statusDetails = UPDATED_AWARD_STATUS_DETAILS,
            jsonData = UPDATED_JSON_DATA
        )

        val exception = assertThrows<SaveEntityException> {
            awardRepository.update(cpid = CPID, updatedAward = updatedAwardEntity)
        }
        assertEquals("Error writing updated award to database.", exception.message)
    }

    @Test
    fun updateSome() {
        insertAward()

        val updatedAwardEntity = AwardEntity(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = UPDATED_AWARD_STATUS,
            statusDetails = UPDATED_AWARD_STATUS_DETAILS,
            jsonData = UPDATED_JSON_DATA
        )
        awardRepository.update(cpid = CPID, updatedAwards = listOf(updatedAwardEntity))

        val actualFundedAward = awardRepository.findBy(cpid = CPID, ocid = OCID, token = TOKEN)
            .orThrow { it.exception }

        assertNotNull(actualFundedAward)
        assertEquals(updatedAwardEntity, actualFundedAward)
    }

    @Test
    fun recordForUpdateSomeNotFound() {
        val updatedAwardEntity = AwardEntity(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = UPDATED_AWARD_STATUS,
            statusDetails = UPDATED_AWARD_STATUS_DETAILS,
            jsonData = UPDATED_JSON_DATA
        )
        val exception = assertThrows<SaveEntityException> {
            awardRepository.update(cpid = CPID, updatedAwards = listOf(updatedAwardEntity))
        }

        assertEquals(
            "An error occurred when writing a record(s) of the awards by cpid '$CPID' to the database. Record(s) is not exists.",
            exception.message
        )
    }

    @Test
    fun errorUpdateSome() {
        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BatchStatement>())

        val updatedAwardEntity = AwardEntity(
            cpid = CPID,
            ocid = OCID,
            token = TOKEN,
            owner = OWNER,
            status = UPDATED_AWARD_STATUS,
            statusDetails = UPDATED_AWARD_STATUS_DETAILS,
            jsonData = UPDATED_JSON_DATA
        )

        val exception = assertThrows<SaveEntityException> {
            awardRepository.update(cpid = CPID, updatedAwards = listOf(updatedAwardEntity))
        }
        assertEquals("Error writing updated award to database.", exception.message)
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
                CREATE TABLE IF NOT EXISTS ${Database.KEYSPACE}.${Database.Awards.TABLE_NAME} (
                    cpid           TEXT,
                    ocid           TEXT,
                    token_entity   TEXT,
                    status         TEXT,
                    status_details TEXT,
                    owner          TEXT,
                    json_data      TEXT,
                    PRIMARY KEY (cpid, ocid, token_entity)
                  );
            """
        )
    }

    private fun expectedFundedAward() = AwardEntity(
        cpid = CPID,
        ocid = OCID,
        token = TOKEN,
        owner = OWNER,
        status = AWARD_STATUS,
        statusDetails = AWARD_STATUS_DETAILS,
        jsonData = JSON_DATA
    )

    private fun insertAward(
        status: AwardStatus = AWARD_STATUS,
        statusDetails: AwardStatusDetails = AWARD_STATUS_DETAILS,
        jsonData: String = JSON_DATA
    ) {
        val rec = QueryBuilder.insertInto(Database.KEYSPACE, Database.Awards.TABLE_NAME)
            .value(Database.Awards.CPID, CPID.underlying)
            .value(Database.Awards.OCID, OCID.underlying)
            .value(Database.Awards.TOKEN_ENTITY, TOKEN.toString())
            .value(Database.Awards.OWNER, OWNER.toString())
            .value(Database.Awards.STATUS, status.toString())
            .value(Database.Awards.STATUS_DETAILS, statusDetails.toString())
            .value(Database.Awards.JSON_DATA, jsonData)
        session.execute(rec)
    }
}
