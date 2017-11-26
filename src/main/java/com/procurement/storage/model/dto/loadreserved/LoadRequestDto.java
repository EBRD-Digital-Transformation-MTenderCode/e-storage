package com.procurement.storage.model.dto.loadreserved;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;

@Getter
@JsonPropertyOrder({
    "bpTypeId",
    "bpTypeName",
    "file"
})
public class LoadRequestDto {

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
