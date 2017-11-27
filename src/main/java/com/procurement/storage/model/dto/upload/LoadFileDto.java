package com.procurement.storage.model.dto.upload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
    "id",
    "fileName",
    "fileLink"
})
public class LoadFileDto {
    @JsonProperty("id")
    @JsonPropertyDescription("Id of the file in DB")
    private Long id;

    @JsonProperty("fileName")
    @JsonPropertyDescription("Name of the file")
    private String fileName;

    @JsonProperty("fileLink")
    @JsonPropertyDescription("Link to the file")
    private String fileLink;
}
