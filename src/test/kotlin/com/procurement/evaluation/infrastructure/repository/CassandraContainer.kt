package com.procurement.evaluation.infrastructure.repository

import com.procurement.evaluation.utils.readFile
import org.testcontainers.containers.wait.strategy.Wait

object CassandraContainer {

    private val initialScripts = readFile("docs/data.cql")

    val container: CassandraTestContainer = CassandraTestContainer("3.11").apply { run() }
        get() {
            CassandraContainerInteractor(field).cqlsh(initialScripts)
            return field
        }

    private fun CassandraTestContainer.run() {
        setWaitStrategy(Wait.forListeningPort())
        start()
    }
}