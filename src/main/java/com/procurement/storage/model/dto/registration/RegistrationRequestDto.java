package com.procurement.storage.model.dto.registration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
    "hash",
    "weight",
    "fileName"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegistrationRequestDto {

    @JsonProperty("hash")
    @JsonPropertyDescription("File hash")
    private String hash;

    @JsonProperty("weight")
    @JsonPropertyDescription("File weight")
    private Integer weight;

    @JsonProperty("fileName")
    @JsonPropertyDescription("File name")
    private String fileName;
}
