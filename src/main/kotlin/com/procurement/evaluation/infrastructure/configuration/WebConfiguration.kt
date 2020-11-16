package com.procurement.evaluation.infrastructure.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableSwagger2
@ComponentScan(
    basePackages = [
        "com.procurement.evaluation.infrastructure.web.controller"
    ]
)
class WebConfiguration
