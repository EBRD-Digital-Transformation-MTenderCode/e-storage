package com.procurement.storage.infrastructure.handler.check.registration

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.storage.domain.model.document.DocumentId

data class CheckRegistrationRequest(
    @param:JsonProperty("documentIds") @field:JsonProperty("documentIds") val documentIds: List<DocumentId>
)
