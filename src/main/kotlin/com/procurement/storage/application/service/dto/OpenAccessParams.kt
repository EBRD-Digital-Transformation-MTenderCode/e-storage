package com.procurement.storage.application.service.dto

import com.procurement.storage.domain.fail.error.DataErrors
import com.procurement.storage.domain.model.document.DocumentId
import com.procurement.storage.domain.util.Result

class OpenAccessParams private constructor(val documentIds: List<DocumentId>) {
    companion object {
        fun tryCreate(documentIds: List<DocumentId>): Result<OpenAccessParams, List<DataErrors>> {
            if (documentIds.isEmpty()) {
                return Result.failure(listOf(DataErrors.Validation.EmptyArray("documentIds")))
            }
            return Result.success(OpenAccessParams(documentIds = documentIds.toList()))
        }
    }
}