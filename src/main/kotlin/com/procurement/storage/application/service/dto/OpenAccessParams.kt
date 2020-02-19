package com.procurement.storage.application.service.dto

import com.procurement.storage.domain.model.document.DocumentId

data class OpenAccessParams (
    val documentIds: List<DocumentId>
)