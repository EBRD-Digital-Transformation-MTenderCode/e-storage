package com.procurement.storage.application.service.dto

import com.procurement.storage.domain.model.document.DocumentId
import java.time.LocalDateTime

data class OpenAccessResult(
    val id: DocumentId,
    val datePublished: LocalDateTime,
    val uri: String
)
