package com.procurement.evaluation.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;


@Configuration
@ComponentScan(basePackages = "com.procurement.evaluation.model.entity")
@EnableCassandraRepositories(basePackages = "com.procurement.evaluation.repository")
public class DatabaseMappingConfig {

}