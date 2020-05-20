package com.procurement.evaluation.infrastructure.configuration.properties

import com.procurement.evaluation.config.properties.GlobalProperties
import com.procurement.evaluation.infrastructure.dto.ApiVersion2
import com.procurement.evaluation.infrastructure.io.orThrow
import java.util.*

object GlobalProperties2 {
    val service = Service()

    object App {
        val apiVersion = ApiVersion2(major = 1, minor = 0, patch = 0)
    }

    class Service {
        val id: String = "7"
        val name: String = "e-evaluation"
        val version: String = loadVersion()

        private fun loadVersion(): String {
            val gitProps: Properties = try {
                GlobalProperties::class.java.getResourceAsStream("/git.properties")
                    .use { stream ->
                        Properties().apply { load(stream) }
                    }
            } catch (expected: Exception) {
                throw IllegalStateException(expected)
            }
            return gitProps.orThrow("git.commit.id.abbrev")
        }
    }
}