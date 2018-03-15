package com.procurement.storage.model.dto.registration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
    @NotNull
    @JsonPropertyDescription("File hash")
    private String hash;

    @JsonProperty("weight")
    @NotNull
    @JsonPropertyDescription("File weight")
    @Min(1)
    private Long weight;

    @JsonProperty("fileName")
    @NotNull
    @JsonPropertyDescription("File name")
    private String fileName;
}
