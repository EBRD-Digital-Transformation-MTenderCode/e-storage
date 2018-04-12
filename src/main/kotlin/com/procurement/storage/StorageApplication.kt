package com.procurement.storage

import com.procurement.storage.config.ApplicationConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication(scanBasePackageClasses = [ApplicationConfig::class])
@EnableEurekaClient
class StorageApplication

fun main(args: Array<String>) {
    runApplication<StorageApplication>(*args)
}
