package com.procurement.evaluation.infrastructure.repository

import org.testcontainers.containers.Container

class CassandraContainerInteractor(
    private val container: CassandraTestContainer
) {

    fun dropKeyspace() {
        cqlsh("DROP KEYSPACE ${Database.KEYSPACE};")
    }

    fun getTables(keyspace: String) =
        cqlsh("USE $keyspace; DESC tables;")
            .stdout
            .trim()
            .split(" ")
            .filter { it.isNotEmpty() }

    fun cqlsh(command: String): Container.ExecResult =
        container.execInContainer("cqlsh", "-e", command)

}
