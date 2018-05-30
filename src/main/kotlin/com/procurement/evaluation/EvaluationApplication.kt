package com.procurement.evaluation

import com.procurement.evaluation.config.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfig::class])
@EnableEurekaClient
class EvaluationApplication

fun main(args: Array<String>) {
    runApplication<EvaluationApplication>(*args)
}