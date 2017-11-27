package com.procurement.storage.model.dto.upload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Setter;

@Setter
@JsonPropertyOrder({
    "errorCode",
    "errorMessage",
    "message"
})
public class LoadResponseDto {

    @JsonProperty("errorCode")
    @JsonPropertyDescription("Error code")
    private String errorCode;

    @JsonProperty("errorMessage")
    @JsonPropertyDescription("Error message")
    private String errorMessage;

    @JsonProperty("message")
    @JsonPropertyDescription("Response message")
    private LoadMessageDto message;

    @JsonCreator
    public LoadResponseDto(@JsonProperty("errorCode") final String errorCode,
                           @JsonProperty("errorMessage") final String errorMessage,
                           @JsonProperty("message") final LoadMessageDto message) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.message = message;
    }
}
