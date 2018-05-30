package com.procurement.evaluation.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(WebConfig::class, DaoConfiguration::class, ServiceConfig::class, ObjectMapperConfig::class)
class ApplicationConfig