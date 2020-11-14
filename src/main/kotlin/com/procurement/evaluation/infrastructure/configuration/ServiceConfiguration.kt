package com.procurement.evaluation.infrastructure.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.evaluation.application.service",
        "com.procurement.evaluation.infrastructure.service",
        "com.procurement.evaluation.infrastructure.handler"
    ]
)
class ServiceConfiguration
