package com.procurement.storage.model.dto.registration

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import javax.validation.constraints.NotNull

@JsonPropertyOrder("hash", "weight", "fileName")
data class RegistrationRequestDto(

        @JsonProperty("hash")
        val hash: String,

        @JsonProperty("weight")
        @NotNull
        val weight: Long,

        @JsonProperty("fileName")
        @NotNull
        val fileName: String
)
