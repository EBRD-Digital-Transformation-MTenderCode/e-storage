package com.procurement.storage.databinding

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.storage.infrastructure.bind.date.DateFormatter
import java.io.IOException
import java.time.LocalDateTime

class JsonDateSerializer : JsonSerializer<LocalDateTime>() {

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(date: LocalDateTime, jsonGenerator: JsonGenerator, provider: SerializerProvider) {
        jsonGenerator.writeString(date.format(DateFormatter.formatter))
    }
}