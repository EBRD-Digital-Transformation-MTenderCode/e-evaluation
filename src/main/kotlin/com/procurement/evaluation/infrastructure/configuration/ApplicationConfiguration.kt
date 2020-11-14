package com.procurement.evaluation.infrastructure.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    DaoConfiguration::class,
    LoggerConfiguration::class,
    ObjectMapperConfiguration::class,
    ServiceConfiguration::class,
    WebConfiguration::class,
)
class ApplicationConfiguration
