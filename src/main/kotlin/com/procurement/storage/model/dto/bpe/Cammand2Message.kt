package com.procurement.storage.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import com.procurement.storage.config.GlobalProperties
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.fail.error.DataErrors
import com.procurement.storage.domain.util.Action
import com.procurement.storage.domain.util.Result
import com.procurement.storage.domain.util.ValidationResult
import com.procurement.storage.domain.util.bind
import com.procurement.storage.infrastructure.web.dto.ApiErrorResponse
import com.procurement.storage.infrastructure.web.dto.ApiIncidentResponse
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiVersion
import com.procurement.storage.utils.tryToObject
import java.time.LocalDateTime
import java.util.*

enum class Command2Type(@JsonValue override val value: String) : Action {
    CHECK_REGISTRATION("checkRegistration"),
    OPEN_ACCESS("openAccess");

    companion object {
        private val elements: Map<String, Command2Type> = values().associateBy { it.value.toUpperCase() }

        fun tryOf(value: String): Result<Command2Type, String> = elements[value.toUpperCase()]
            ?.let {
                Result.success(it)
            }
            ?: Result.failure(
                "Unknown value for enumType ${Command2Type::class.java.canonicalName}: " +
                    "$value, Allowed values are ${values().joinToString { it.value }}"
            )
    }

    override fun toString() = value
}

fun errorResponse(exception: Fail, id: UUID, version: ApiVersion): ApiResponse =
    when (exception) {
        is Fail.Error -> getApiFailResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.description
        )
        is Fail.Incident -> getApiIncidentResponse(
            id = id,
            version = version,
            code = "00.00",
            message = exception.description
        )
    }

private fun getApiFailResponse(
    id: UUID,
    version: ApiVersion,
    code: String,
    message: String
): ApiErrorResponse {
    return ApiErrorResponse(
        id = id,
        version = version,
        result = listOf(
            ApiErrorResponse.Error(
                code = "400.${GlobalProperties.service.id}." + code,
                description = message
            )
        )
    )
}

private fun getApiIncidentResponse(
    id: UUID,
    version: ApiVersion,
    code: String,
    message: String
): ApiIncidentResponse {
    return ApiIncidentResponse(
        id = id,
        version = version,
        result = ApiIncidentResponse.Incident(
            id = UUID.randomUUID(),
            date = LocalDateTime.now(),
            errors = listOf(
                ApiIncidentResponse.Incident.Error(
                    code = "400.${GlobalProperties.service.id}." + code,
                    description = message,
                    metadata = null
                )
            ),
            service = ApiIncidentResponse.Incident.Service(
                id = GlobalProperties.service.id,
                version = GlobalProperties.service.version,
                name = GlobalProperties.service.name
            )
        )
    )
}

val NaN: UUID
    get() = UUID(0, 0)

fun JsonNode.getId(): Result<UUID, DataErrors> {
    return this.getAttribute("id")
        .bind {
            val value = it.asText()
            asUUID(value)
        }
}

fun JsonNode.getVersion(): Result<ApiVersion, DataErrors> {
    return this.getAttribute("version")
        .bind {
            val value = it.asText()
            when (val result = ApiVersion.tryOf(value)) {
                is Result.Success -> result
                is Result.Failure -> result.mapError {
                    DataErrors.DataFormatMismatch(result.error)
                }
            }
        }
}

fun JsonNode.getAction(): Result<Command2Type, DataErrors> {
    return this.getAttribute("action")
        .bind {
            val value = it.asText()
            when (val result = Command2Type.tryOf(value)) {
                is Result.Success -> result
                is Result.Failure -> result.mapError {
                    DataErrors.UnknownValue(result.error)
                }
            }
        }
}

private fun asUUID(value: String): Result<UUID, DataErrors> =
    try {
        Result.success<UUID>(UUID.fromString(value))
    } catch (exception: IllegalArgumentException) {
        Result.failure(DataErrors.DataFormatMismatch(value))
    }

fun JsonNode.getAttribute(name: String): Result<JsonNode, DataErrors> {
    return if (has(name)) {
        val attr = get(name)
        if (attr !is NullNode)
            Result.success(attr)
        else
            Result.failure(DataErrors.DataTypeMismatch("$attr"))
    } else
        Result.failure(
            DataErrors.MissingRequiredAttribute(name)
        )
}

fun <T : Any> JsonNode.tryGetParams(target: Class<T>): Result<T, DataErrors> =
    getAttribute("params").bind { node ->
        when (val result = node.tryToObject(target)) {
            is Result.Success -> result
            is Result.Failure -> result.mapError {
                DataErrors.DataFormatMismatch(result.error)
            }
        }
    }

fun JsonNode.hasParams(): ValidationResult<DataErrors> =
    if (this.has("params"))
        ValidationResult.ok()
    else
        ValidationResult.error(
            DataErrors.MissingRequiredAttribute("params")
        )
