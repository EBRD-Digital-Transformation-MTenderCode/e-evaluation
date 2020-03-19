package com.procurement.evaluation.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.evaluation.service",
        "com.procurement.evaluation.application.service",
        "com.procurement.evaluation.infrastructure.service"
    ]
)
class ServiceConfig
