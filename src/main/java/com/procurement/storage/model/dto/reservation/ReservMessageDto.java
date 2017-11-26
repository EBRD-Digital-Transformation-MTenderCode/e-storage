package com.procurement.storage.model.dto.reservation;

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
public class ReservMessageDto {

    @JsonProperty("bpTypeId")
    @JsonPropertyDescription("Business process id")
    private String bpTypeId;

    @JsonProperty("bpTypeName")
    @JsonPropertyDescription("Business process name")
    private String bpTypeName;

    @JsonProperty("file")
    @JsonPropertyDescription("File data")
    private ReservFileDto file;

}
