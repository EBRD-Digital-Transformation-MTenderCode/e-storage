package com.procurement.storage.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.storage.domain.model.document.DocumentId

data class OpenAccessRequest(
    @param:JsonProperty("documentIds") @field:JsonProperty("documentIds") val documentIds: List<DocumentId>
)
