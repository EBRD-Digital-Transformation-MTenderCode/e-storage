package com.procurement.storage.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.procurement.storage.infrastructure.bind.jackson.configuration
import org.springframework.context.annotation.Configuration
import javax.annotation.PostConstruct

@Configuration
class JsonConfig(private val mapper: ObjectMapper) {

    @PostConstruct
    fun init() {
        mapper.apply {
            configuration()
        }
    }
}
