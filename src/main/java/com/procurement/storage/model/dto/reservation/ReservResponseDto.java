package com.procurement.storage.model.dto.reservation;

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
public class ReservResponseDto {

    @JsonProperty("errorCode")
    @JsonPropertyDescription("Error code")
    private String errorCode;

    @JsonProperty("errorMessage")
    @JsonPropertyDescription("Error message")
    private String errorMessage;

    @JsonProperty("message")
    @JsonPropertyDescription("Response message")
    private ReservMessageDto message;

    @JsonCreator
    public ReservResponseDto(@JsonProperty("errorCode") final String errorCode,
                             @JsonProperty("errorMessage") final String errorMessage,
                             @JsonProperty("message") final ReservMessageDto message) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.message = message;
    }
}
