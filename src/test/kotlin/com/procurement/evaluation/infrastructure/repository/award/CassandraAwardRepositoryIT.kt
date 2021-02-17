package com.procurement.evaluation.infrastructure.repository.award

import com.datastax.driver.core.BatchStatement
import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.clearInvocations
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.evaluation.application.repository.award.AwardRepository
import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.failure
import com.procurement.evaluation.infrastructure.repository.CassandraContainer
import com.procurement.evaluation.infrastructure.repository.CassandraTestContainer
import com.procurement.evaluation.infrastructure.repository.Database
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*

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

        private var container: CassandraTestContainer = CassandraContainer.container

        private val poolingOptions = PoolingOptions()
            .setMaxConnectionsPerHost(HostDistance.LOCAL, 1)
        private val cluster = Cluster.builder()
            .addContactPoints(container.contractPoint)
            .withPort(container.port)
            .withoutJMXReporting()
            .withPoolingOptions(poolingOptions)
            .withAuthProvider(PlainTextAuthProvider(container.username, container.password))
            .build()
    }

    private var session: Session = spy(cluster.connect())
    private var awardRepository: AwardRepository = CassandraAwardRepository(session)

    @AfterEach
    fun clean() {
        clearTables()
        clearInvocations(session)
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

        val failure = awardRepository.findBy(cpid = CPID).failure()

        assertTrue(failure.exception is RuntimeException)
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

        val failure = awardRepository.findBy(cpid = CPID, ocid = OCID).failure()

        assertTrue(failure.exception is RuntimeException)
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

        val failure = awardRepository.findBy(cpid = CPID, ocid = OCID, token = TOKEN).failure()

        assertTrue(failure.exception is RuntimeException)
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
        awardRepository.save(cpid = CPID, award = awardEntity)

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
        awardRepository.save(cpid = CPID, award = awardEntity)

        val result = awardRepository.save(cpid = CPID, award = awardEntity)

        assertTrue(result.isSuccess)
        val wasApplied = result.get
        assertFalse(wasApplied)
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

        val failure = awardRepository.save(cpid = CPID, award = awardEntity).failure()

        assertTrue(failure.exception is RuntimeException)
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

        val result = awardRepository.update(cpid = CPID, updatedAward = updatedAwardEntity)
            .orThrow { it.exception }

        assertFalse(result)
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

        val failure = awardRepository.update(cpid = CPID, updatedAward = updatedAwardEntity).failure()

        assertTrue(failure.exception is RuntimeException)
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
        val result = awardRepository.update(cpid = CPID, updatedAwards = listOf(updatedAwardEntity))

        assertTrue(result.isSuccess)
        val wasApplied = result.get
        assertFalse(wasApplied)
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

        val failure = awardRepository.update(cpid = CPID, updatedAwards = listOf(updatedAwardEntity)).failure()

        assertTrue(failure.exception is RuntimeException)
    }

    private fun clearTables() {
        session.execute("TRUNCATE ${Database.KEYSPACE}.${Database.Awards.TABLE_NAME}")
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
