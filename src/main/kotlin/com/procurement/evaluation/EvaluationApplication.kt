package com.procurement.evaluation

import com.procurement.evaluation.config.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfig::class])
class EvaluationApplication

fun main(args: Array<String>) {
    runApplication<EvaluationApplication>(*args)
}