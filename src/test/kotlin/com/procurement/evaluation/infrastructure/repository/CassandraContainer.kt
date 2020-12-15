package com.procurement.evaluation.infrastructure.repository

import org.testcontainers.containers.wait.strategy.Wait

object CassandraContainer {

    val container: CassandraTestContainer = CassandraTestContainer("3.11")
        .apply {
            setWaitStrategy(Wait.forListeningPort())
            start()
        }
}
