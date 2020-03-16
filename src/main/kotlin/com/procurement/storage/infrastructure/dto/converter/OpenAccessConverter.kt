package com.procurement.storage.infrastructure.dto.converter

import com.procurement.storage.application.service.dto.OpenAccessParams
import com.procurement.storage.domain.fail.error.DataErrors
import com.procurement.storage.domain.util.Result
import com.procurement.storage.infrastructure.handler.open.OpenAccessRequest

fun OpenAccessRequest.convert(): Result<OpenAccessParams, DataErrors> =
    OpenAccessParams.tryCreate(documentIds = this.documentIds)

