package com.procurement.storage.infrastructure.handler.open

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.storage.domain.model.document.DocumentId

data class OpenAccessRequest(
    @param:JsonProperty("documentIds") @field:JsonProperty("documentIds") val documentIds: List<DocumentId>
)
