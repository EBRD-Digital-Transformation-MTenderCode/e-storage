package com.procurement.storage.infrastructure.dto.converter

import com.procurement.storage.application.service.dto.CheckRegistrationParams
import com.procurement.storage.exception.BpeErrorException
import com.procurement.storage.exception.ErrorType
import com.procurement.storage.infrastructure.dto.CheckRegistrationRequest
import com.procurement.storage.lib.mapIfNotEmpty
import com.procurement.storage.lib.orThrow

fun CheckRegistrationRequest.convert() = CheckRegistrationParams(
    documentIds = this.documentIds
        .mapIfNotEmpty { id ->
            id
        }
        .orThrow {
            BpeErrorException(
                error = ErrorType.IS_EMPTY,
                message = "CheckRegistrationRequest.documentIds list is empty"
            )
        }
)
