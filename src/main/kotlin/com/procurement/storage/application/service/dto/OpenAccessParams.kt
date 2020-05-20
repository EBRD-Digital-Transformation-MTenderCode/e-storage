package com.procurement.storage.application.service.dto

import com.procurement.storage.application.model.parseDate
import com.procurement.storage.domain.fail.error.DataErrors
import com.procurement.storage.domain.model.document.DocumentId
import com.procurement.storage.domain.util.Result
import java.time.LocalDateTime

class OpenAccessParams private constructor(val documentIds: List<DocumentId>, val datePublished: LocalDateTime) {
    companion object {
        fun tryCreate(documentIds: List<DocumentId>, datePublished: String): Result<OpenAccessParams, DataErrors> {
            if (documentIds.isEmpty()) {
                return Result.failure(DataErrors.Validation.EmptyArray("documentIds"))
            }

            val datePublishedParsed = parseDate(value = datePublished, attributeName = "datePublished")
                .doOnError { error -> return Result.failure(error) }
                .get

            return Result.success(
                OpenAccessParams(documentIds = documentIds.toList(), datePublished = datePublishedParsed)
            )
        }
    }
}