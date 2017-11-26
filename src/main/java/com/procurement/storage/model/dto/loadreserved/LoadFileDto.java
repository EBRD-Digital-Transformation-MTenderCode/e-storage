package com.procurement.storage.model.dto.loadreserved;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@JsonPropertyOrder({
    "id",
    "fileName",
    "fileLink",
    "fileBody"
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

    @JsonProperty("fileBody")
    @JsonPropertyDescription("body of the file")
    private MultipartFile fileBody;
}
