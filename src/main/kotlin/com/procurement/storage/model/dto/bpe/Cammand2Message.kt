package com.procurement.storage.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.config.GlobalProperties
import com.procurement.storage.exception.BpeErrorException
import com.procurement.storage.exception.EnumException
import com.procurement.storage.exception.ExternalException
import com.procurement.storage.infrastructure.web.dto.ApiFailResponse
import com.procurement.storage.infrastructure.web.dto.ApiIncidentResponse
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiVersion
import com.procurement.storage.utils.getBy
import java.time.LocalDateTime
import java.util.*

enum class Command2Type(@JsonValue private val value: String) {
    CHECK_REGISTRATION("checkRegistration"),
    OPEN_ACCESS("openAccess");

    companion object {
        private val elements: Map<String, Command2Type> = values().associateBy { it.value.toUpperCase() }

        fun fromString(value: String): Command2Type = elements[value.toUpperCase()]
            ?: throw EnumException(
                enumType = Command2Type::class.java.canonicalName,
                value = value,
                values = values().joinToString { it.value }
            )
    }

    override fun toString() = value
}

fun errorResponse(exception: Exception, id: UUID = NaN, version: ApiVersion): ApiResponse =
    when (exception) {
        is BpeErrorException -> getApiFailResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.message!!
        )
        is EnumException  -> getApiFailResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.message!!
        )
        is ExternalException  -> getApiFailResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.message!!
        )
        else              -> getApiIncidentResponse(
            id = id,
            version = version,
            code = "00.00",
            message = exception.message ?: "Internal server error"
        )
    }

private fun getApiFailResponse(
    id: UUID,
    version: ApiVersion,
    code: String,
    message: String
): ApiFailResponse {
    return ApiFailResponse(
        id = id,
        version = version,
        result = listOf(
            ApiFailResponse.Error(
                code = "400.${GlobalProperties.serviceId}." + code,
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
                    code = "400.${GlobalProperties.serviceId}." + code,
                    description = message,
                    metadata = null
                )
            ),
            service = ApiIncidentResponse.Incident.Service(
                id = GlobalProperties.serviceId,
                version = GlobalProperties.App.apiVersion,
                name = GlobalProperties.serviceName
            )
        )
    )
}

val NaN: UUID
    get() = UUID(0, 0)

fun JsonNode.getId(): UUID {
    return UUID.fromString(this.getBy("id").asText())
}

fun JsonNode.getAction(): Command2Type {
    return Command2Type.fromString(this.getBy("action").asText())
}

fun JsonNode.getVersion(): ApiVersion {
    return ApiVersion.valueOf(this.getBy("version").asText())
}

fun JsonNode.getParams(): JsonNode {
    return this.getBy("params")
}
