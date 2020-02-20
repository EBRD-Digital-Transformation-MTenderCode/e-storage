package com.procurement.storage.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.storage.databinding.JsonDateDeserializer
import com.procurement.storage.databinding.JsonDateSerializer
import com.procurement.storage.domain.model.document.DocumentId
import java.time.LocalDateTime

data class OpenAccessResponse(
    @param:JsonProperty("id") @field:JsonProperty("id") val id: DocumentId,

    @JsonSerialize(using = JsonDateSerializer::class)
    @JsonDeserialize(using = JsonDateDeserializer::class)
    @param:JsonProperty("datePublished") @field:JsonProperty("datePublished") val datePublished: LocalDateTime,

    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
)
