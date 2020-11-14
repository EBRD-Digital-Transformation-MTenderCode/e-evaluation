package com.procurement.evaluation.infrastructure.configuration

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.infrastructure.service.CustomLogger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class LoggerConfiguration {

    @Bean
    fun logger(): Logger = CustomLogger()
}