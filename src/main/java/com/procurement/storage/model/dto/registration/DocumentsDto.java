package com.procurement.storage.model.dto.registration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;
import javax.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonPropertyOrder({
        "documents"
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentsDto {

    @JsonProperty("documents")
    @Valid
    private final List<DocumentDto> documents;

    @JsonCreator
    public DocumentsDto(@JsonProperty("documents") final List<DocumentDto> documents) {
        this.documents = documents;
    }

}