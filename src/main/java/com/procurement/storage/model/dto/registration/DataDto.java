package com.procurement.storage.model.dto.registration;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.storage.databinding.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataDto {

    @JsonProperty("id")
    private String id;

    @JsonProperty("url")
    private String url;

    @JsonProperty("dateModified")
    private LocalDateTime dateModified;

    @JsonProperty("datePublished")
    private LocalDateTime datePublished;

    @JsonCreator
    public DataDto(@JsonProperty("id") final String id,
                   @JsonProperty("url") final String url,
                   @JsonProperty("dateModified")
                   @JsonSerialize(using = LocalDateTimeSerializer.class) final LocalDateTime dateModified,
                   @JsonProperty("datePublished")
                   @JsonSerialize(using = LocalDateTimeSerializer.class) final LocalDateTime datePublished) {
        this.id = id;
        this.url = url;
        this.dateModified = dateModified;
        this.datePublished = datePublished;
    }
}
