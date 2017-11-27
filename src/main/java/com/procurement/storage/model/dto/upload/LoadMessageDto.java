package com.procurement.storage.model.dto.upload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Setter;

@Setter
@JsonPropertyOrder({
    "bpTypeId",
    "bpTypeName",
    "file"
})
public class LoadMessageDto {

    @JsonProperty("bpTypeId")
    @JsonPropertyDescription("Business process id")
    private String bpTypeId;

    @JsonProperty("bpTypeName")
    @JsonPropertyDescription("Business process name")
    private String bpTypeName;

    @JsonProperty("file")
    @JsonPropertyDescription("File data")
    private LoadFileDto file;

}
