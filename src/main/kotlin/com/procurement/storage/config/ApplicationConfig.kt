package com.procurement.storage.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import


@Configuration
@Import(CassandraConfig::class, WebConfig::class, JsonConfig::class)
class ApplicationConfig
