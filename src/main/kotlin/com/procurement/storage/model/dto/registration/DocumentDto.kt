package com.procurement.storage.model.dto.registration

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.point.databinding.JsonDateSerializer
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

@JsonPropertyOrder(
        "id",
        "documentType",
        "title",
        "description",
        "url",
        "datePublished",
        "dateModified",
        "format",
        "language",
        "relatedLots"
)
data class DocumentDto(

        @JsonProperty("id")
        @NotNull
        val id: String,

        @JsonProperty("documentType")
        val documentType: String?,

        @JsonProperty("title")
        val title: String?,

        @JsonProperty("description")
        val description: String?,

        @JsonProperty("url")
        var url: String?,

        @JsonProperty("datePublished")
        @JsonSerialize(using = JsonDateSerializer::class)
        var datePublished: LocalDateTime?,

        @JsonProperty("dateModified")
        @JsonSerialize(using = JsonDateSerializer::class)
        val dateModified: LocalDateTime?,

        @JsonProperty("format")
        val format: String?,

        @JsonProperty("language")
        val language: String?,

        @JsonProperty("relatedLots")
        val relatedLots: List<String>?
)