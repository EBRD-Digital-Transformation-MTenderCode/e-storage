package com.procurement.storage.model.dto.registration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.storage.databinding.LocalDateTimeDeserializer;
import com.procurement.storage.databinding.LocalDateTimeSerializer;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
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
})
public class DocumentDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonProperty("documentType")
    private final String documentType;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("url")
    private final String url;

    @JsonProperty("datePublished")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime datePublished;

    @JsonProperty("dateModified")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private final LocalDateTime dateModified;

    @JsonProperty("format")
    private final String format;

    @JsonProperty("language")
    private final String language;

    @JsonProperty("relatedLots")
    private final List<String> relatedLots;

    @JsonCreator
    public DocumentDto(@JsonProperty("id") final String id,
                       @JsonProperty("documentType") final String documentType,
                       @JsonProperty("title") final String title,
                       @JsonProperty("description") final String description,
                       @JsonProperty("url") final String url,
                       @JsonProperty("datePublished") final LocalDateTime datePublished,
                       @JsonProperty("dateModified") final LocalDateTime dateModified,
                       @JsonProperty("format") final String format,
                       @JsonProperty("language") final String language,
                       @JsonProperty("relatedLots") final List<String> relatedLots) {
        this.id = id;
        this.documentType = documentType;
        this.title = title;
        this.description = description;
        this.url = url;
        this.datePublished = datePublished;
        this.dateModified = dateModified;
        this.format = format;
        this.language = language;
        this.relatedLots = relatedLots;
    }
}