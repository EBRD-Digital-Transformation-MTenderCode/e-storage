package com.procurement.storage.model.dto.registration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.procurement.storage.databinding.LocalDateTimeSerializer;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@JsonPropertyOrder({
        "errorCode",
        "errorMessage",
        "message"
})
public class RegistrationResponseDto {

    @JsonProperty("id")
    @JsonPropertyDescription("File id")
    private String id;

    @JsonProperty("url")
    @JsonPropertyDescription("URL to access the file")
    private String url;

    @JsonProperty("dateModified")
    @JsonPropertyDescription("Вate of file modification")
    private LocalDateTime dateModified;

    @JsonCreator
    public RegistrationResponseDto(@JsonProperty("id") final String id,
                                   @JsonProperty("url") final String url,
                                   @JsonProperty("dateModified")
                                   @JsonSerialize(using = LocalDateTimeSerializer.class) final LocalDateTime dateModified) {
        this.id = id;
        this.url = url;
        this.dateModified = dateModified;
    }
}
