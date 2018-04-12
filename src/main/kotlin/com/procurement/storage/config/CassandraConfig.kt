package com.procurement.storage.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories

@Configuration
@ComponentScan(basePackages = ["com.procurement.storage.model.entity"])
@EnableCassandraRepositories(basePackages = ["com.procurement.storage.repository"])
class CassandraConfig
