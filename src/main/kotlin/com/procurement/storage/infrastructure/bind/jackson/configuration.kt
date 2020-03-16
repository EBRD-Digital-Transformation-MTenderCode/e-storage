package com.procurement.storage.infrastructure.bind.jackson

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.procurement.storage.databinding.JsonDateDeserializer
import com.procurement.storage.databinding.JsonDateSerializer
import com.procurement.storage.infrastructure.bind.apiversion.ApiVersionDeserializer
import com.procurement.storage.infrastructure.bind.apiversion.ApiVersionSerializer
import com.procurement.storage.infrastructure.web.dto.ApiVersion
import java.time.LocalDateTime

fun ObjectMapper.configuration() {
    val module = SimpleModule().apply {
        /**
         * Serializer/Deserializer for LocalDateTime type
         */
        addSerializer(LocalDateTime::class.java, JsonDateSerializer())
        addDeserializer(LocalDateTime::class.java, JsonDateDeserializer())

        addSerializer(ApiVersion::class.java, ApiVersionSerializer())
        addDeserializer(ApiVersion::class.java, ApiVersionDeserializer())

    }

    this.registerModule(module)
    this.registerModule(KotlinModule())
    this.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
    this.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    this.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
}
