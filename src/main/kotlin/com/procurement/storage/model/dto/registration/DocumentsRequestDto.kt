package com.procurement.storage.model.dto.registration

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.Valid

data class DocumentsRequestDto(

        @Valid
        @JsonProperty("documents")
        val documents: List<DocumentDto>

)