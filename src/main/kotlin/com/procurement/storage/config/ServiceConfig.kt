package com.procurement.storage.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(
    basePackages = [
        "com.procurement.storage.service",
        "com.procurement.storage.infrastructure.handlers",
        "com.procurement.storage.application.service"
    ]
)
@EnableConfigurationProperties(UploadFileProperties::class)
class ServiceConfig
