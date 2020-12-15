package com.procurement.evaluation.infrastructure.repository.history

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.Cluster
import com.datastax.driver.core.HostDistance
import com.datastax.driver.core.PlainTextAuthProvider
import com.datastax.driver.core.PoolingOptions
import com.datastax.driver.core.Session
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.clearInvocations
import com.nhaarman.mockito_kotlin.doThrow
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.api.v1.CommandTypeV1
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.handler.HistoryRepository
import com.procurement.evaluation.infrastructure.repository.CassandraContainer
import com.procurement.evaluation.infrastructure.repository.CassandraContainerInteractor
import com.procurement.evaluation.infrastructure.repository.CassandraTestContainer
import com.procurement.evaluation.infrastructure.repository.Database
import com.procurement.evaluation.infrastructure.repository.period.CassandraAwardPeriodRepositoryIT
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.Container
import java.util.*

class CassandraHistoryRepositoryIT {

    companion object {
        private val COMMAND_ID: CommandId = CommandId(UUID.randomUUID().toString())
        private val ACTION: Action = CommandTypeV1.AWARDS_CANCELLATION
        private const val JSON_DATA: String = """{"tender": {"title" : "Tender-Title"}}"""
        private val initialScripts = CassandraAwardPeriodRepositoryIT::class.java.getResource("/data.cql").readText()

        private val container: CassandraTestContainer = CassandraContainer.container
        private val containerInteractor: CassandraContainerInteractor = CassandraContainerInteractor(container)

        private val poolingOptions = PoolingOptions()
            .setMaxConnectionsPerHost(HostDistance.LOCAL, 1)

        private val cluster = Cluster.builder()
            .addContactPoints(container.contractPoint)
            .withPort(container.port)
            .withoutJMXReporting()
            .withPoolingOptions(poolingOptions)
            .withAuthProvider(PlainTextAuthProvider(container.username, container.password))
            .build()

        private fun createKeyspace(): Container.ExecResult = containerInteractor.cqlsh(initialScripts)

        @BeforeAll
        @JvmStatic
        internal fun init() {
            createKeyspace()
        }

    }

    private val session: Session = spy(cluster.connect())
    private val repository: HistoryRepository = CassandraHistoryRepository(session)

    @AfterEach
    fun clean() {
        clearTables()
        clearInvocations(session)
    }

    @Test
    fun saveHistory() {
        val result = repository.saveHistory(commandId = COMMAND_ID, action = ACTION, data = JSON_DATA)

        assertTrue(result.isSuccess)
        result.forEach { wasApplied ->
            assertTrue(wasApplied)
        }
    }

    @Test
    fun errorWhenSaveHistory() {
        val expectedError = Failure.Incident.Database.DatabaseInteractionIncident(RuntimeException())

        doThrow(RuntimeException())
            .whenever(session)
            .execute(any<BoundStatement>())

        val result = repository.saveHistory(commandId = COMMAND_ID, action = ACTION, data = JSON_DATA)

        assertTrue(result.isFail)
        result.doOnError { fail ->
            assertEquals(expectedError.number, fail.number)
            assertEquals(expectedError.exception::class.java, fail.exception::class.java)
        }
    }

    @Test
    fun saveHistoryWhenRecordAlreadyExists() {
        val onFirstSaveResult = repository.saveHistory(commandId = COMMAND_ID, action = ACTION, data = JSON_DATA)
        assertTrue(onFirstSaveResult.isSuccess)
        onFirstSaveResult.forEach { wasApplied ->
            assertTrue(wasApplied)
        }

        val onSecondSaveResult = repository.saveHistory(commandId = COMMAND_ID, action = ACTION, data = JSON_DATA)

        assertTrue(onSecondSaveResult.isSuccess)
        onSecondSaveResult.forEach { wasApplied ->
            assertFalse(wasApplied)
        }
    }

    @Test
    fun getHistory() {
        val savedResult = repository.saveHistory(commandId = COMMAND_ID, action = ACTION, data = JSON_DATA)

        assertTrue(savedResult.isSuccess)
        savedResult.forEach { wasApplied ->
            assertTrue(wasApplied)
        }

        val loadedResult = repository.getHistory(commandId = COMMAND_ID, action = ACTION)
        assertTrue(loadedResult.isSuccess)
        loadedResult.forEach { savedResponse ->
            assertNotNull(savedResponse)
            assertEquals(JSON_DATA, savedResponse)
        }
    }

    private fun clearTables() {
        session.execute("TRUNCATE ${Database.KEYSPACE}.${Database.History.TABLE_NAME};")
    }

}
