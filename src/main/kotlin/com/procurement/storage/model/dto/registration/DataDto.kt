package com.procurement.storage.model.dto.registration

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.point.databinding.JsonDateSerializer
import java.time.LocalDateTime

data class DataDto @JsonCreator constructor(

        @JsonProperty("id")
        val id: String?,

        @JsonProperty("url")
        val url: String?,

        @JsonProperty("dateModified")
        @JsonSerialize(using = JsonDateSerializer::class)
        val dateModified: LocalDateTime?,

        @JsonProperty("datePublished")
        @JsonSerialize(using = JsonDateSerializer::class)
        val datePublished: LocalDateTime?
)

