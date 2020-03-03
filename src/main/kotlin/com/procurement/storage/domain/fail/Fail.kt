package com.procurement.storage.domain.fail

import com.procurement.storage.domain.util.Result
import com.procurement.storage.domain.util.ValidationResult

sealed class Fail {

    abstract class Error(val prefix: String) : Fail() {
        abstract val code: String
        abstract val description: String
        val message: String
            get() = "ERROR CODE: '$code', DESCRIPTION: '$description'."
    }

    sealed class Incident(val code: String, val description: String) : Fail() {

    }
}

fun <T, E : Fail.Error> E.toResult(): Result<T, E> = Result.failure(this)

fun <E : Fail.Error> E.toValidationResult(): ValidationResult<E> = ValidationResult.error(this)
