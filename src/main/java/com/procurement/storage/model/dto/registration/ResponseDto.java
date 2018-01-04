package com.procurement.storage.model.dto.registration;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "data"
})
public class ResponseDto {

    @JsonProperty("data")
    @JsonPropertyDescription("Response data")
    private DataDto data;

    @JsonCreator
    public ResponseDto(@JsonProperty("data") final DataDto data) {
        this.data = data;
    }
}
