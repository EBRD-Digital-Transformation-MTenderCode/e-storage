package com.procurement.storage.infrastructure.dto.converter

import com.procurement.storage.application.service.dto.CheckRegistrationParams
import com.procurement.storage.domain.fail.error.DataErrors
import com.procurement.storage.domain.util.Result
import com.procurement.storage.infrastructure.handler.check.registration.CheckRegistrationRequest

fun CheckRegistrationRequest.convert(): Result<CheckRegistrationParams, DataErrors> {
    return CheckRegistrationParams.tryCreate(documentIds = this.documentIds)
}