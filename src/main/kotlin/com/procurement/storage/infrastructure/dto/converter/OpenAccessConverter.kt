package com.procurement.storage.infrastructure.dto.converter

import com.procurement.storage.application.service.dto.OpenAccessParams
import com.procurement.storage.exception.BpeErrorException
import com.procurement.storage.exception.ErrorType
import com.procurement.storage.infrastructure.dto.OpenAccessRequest
import com.procurement.storage.lib.mapIfNotEmpty
import com.procurement.storage.lib.orThrow

fun OpenAccessRequest.convert() = OpenAccessParams(
    documentIds = this.documentIds
        .mapIfNotEmpty { id ->
            id
        }
        .orThrow {
            BpeErrorException(
                error = ErrorType.IS_EMPTY,
                message = "OpenAccessRequest.documentIds list is empty"
            )
        }
)
