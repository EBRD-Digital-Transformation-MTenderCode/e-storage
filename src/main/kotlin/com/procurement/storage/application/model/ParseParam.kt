package com.procurement.storage.application.model

import com.procurement.storage.domain.fail.error.DataErrors
import com.procurement.storage.domain.model.date.tryParseLocalDateTime
import com.procurement.storage.domain.util.Result
import com.procurement.storage.domain.util.asSuccess
import java.time.LocalDateTime

fun parseDate(value: String, attributeName: String): Result<LocalDateTime, DataErrors.Validation.DataFormatMismatch> =
    value.tryParseLocalDateTime()
        .doOnError { expectedFormat ->
            return Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = attributeName,
                    actualValue = value,
                    expectedFormat = expectedFormat
                )
            )
        }
        .get
        .asSuccess()