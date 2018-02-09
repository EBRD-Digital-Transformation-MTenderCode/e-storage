package com.procurement.storage.model.dto.registration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentDto {
    @JsonProperty("id")
    @NotNull
    private final String id;

    @JsonCreator
    public DocumentDto(@JsonProperty("id") final String id
    ) {
        this.id = id;

    }
}