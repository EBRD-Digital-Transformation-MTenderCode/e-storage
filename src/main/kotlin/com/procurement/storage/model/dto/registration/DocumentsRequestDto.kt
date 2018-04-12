package com.procurement.storage.model.dto.registration

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid


class DocumentsRequestDto(
        @Valid
        @JsonProperty("documents")
        val documents: List<DocumentDto>
)