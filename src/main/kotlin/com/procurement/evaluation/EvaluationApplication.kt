package com.procurement.evaluation

import com.procurement.evaluation.infrastructure.configuration.ApplicationConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfiguration::class])
class EvaluationApplication

fun main(args: Array<String>) {
    runApplication<EvaluationApplication>(*args)
}
