package com.procurement.storage.model.dto.reservation;

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
    "fileMd5Sum",
    "fileSize",
    "fileLink",
    "description",
    "open"
})
public class ReservFileDto {
    @JsonProperty("id")
    @JsonPropertyDescription("Id of the file in DB")
    private Long id;

    @JsonProperty("fileName")
    @JsonPropertyDescription("Name of the file")
    private String fileName;

    @JsonProperty("fileHash")
    @JsonPropertyDescription("Md5 sum of the file")
    private String fileHash;

    @JsonProperty("fileSize")
    @JsonPropertyDescription("Size of the file")
    private Integer fileSize;

    @JsonProperty("fileLink")
    @JsonPropertyDescription("Link to the file")
    private String fileLink;

    @JsonProperty("description")
    @JsonPropertyDescription("Description of the file")
    private String description;

    @JsonProperty("open")
    @JsonPropertyDescription("property of the file")
    private Boolean open;
}
