package com.procurement.storage.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeType
import com.fasterxml.jackson.databind.node.NullNode
import com.procurement.storage.config.GlobalProperties
import com.procurement.storage.domain.EnumElementProvider
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.fail.error.BadRequestErrors
import com.procurement.storage.domain.fail.error.DataErrors
import com.procurement.storage.domain.util.Action
import com.procurement.storage.domain.util.Result
import com.procurement.storage.domain.util.Result.Companion.failure
import com.procurement.storage.domain.util.Result.Companion.success
import com.procurement.storage.domain.util.asSuccess
import com.procurement.storage.domain.util.bind
import com.procurement.storage.infrastructure.web.dto.ApiDataErrorResponse
import com.procurement.storage.infrastructure.web.dto.ApiErrorResponse
import com.procurement.storage.infrastructure.web.dto.ApiIncidentResponse
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiVersion
import com.procurement.storage.utils.tryToObject
import java.time.LocalDateTime
import java.util.*

enum class Command2Type(@JsonValue override val key: String) : Action, EnumElementProvider.Key {
    CHECK_REGISTRATION("checkRegistration"),
    OPEN_ACCESS("openAccess");

    override fun toString(): String = key

    companion object : EnumElementProvider<Command2Type>(info = info()) {

        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Command2Type.orThrow(name)
    }
}

fun errorResponse(fail: Fail, id: UUID = NaN, version: ApiVersion = GlobalProperties.App.apiVersion): ApiResponse =
    when (fail) {
        is DataErrors.Validation -> generateDataErrorResponse(id = id, version = version, fail = fail)
        is Fail.Error -> generateErrorResponse(id = id, version = version, fail = fail)
        is Fail.Incident -> generateIncidentResponse(id = id, version = version, fail = fail)
    }

fun generateDataErrorResponse(id: UUID, version: ApiVersion, fail: DataErrors.Validation): ApiDataErrorResponse =
    ApiDataErrorResponse(
        version = version,
        id = id,
        result = listOf(
            ApiDataErrorResponse.Error(
                code = "${fail.code}/${GlobalProperties.service.id}",
                description = fail.description,
                attributeName = fail.name
            )
        )
    )

fun generateErrorResponse(id: UUID, version: ApiVersion, fail: Fail.Error): ApiErrorResponse =
    ApiErrorResponse(
        version = version,
        id = id,
        result = listOf(
            ApiErrorResponse.Error(
                code = "${fail.code}/${GlobalProperties.service.id}",
                description = fail.description
            )
        )
    )

fun generateIncidentResponse(id: UUID, version: ApiVersion, fail: Fail.Incident): ApiIncidentResponse =
    ApiIncidentResponse(
        id = id,
        version = version,
        result = ApiIncidentResponse.Incident(
            id = UUID.randomUUID(),
            date = LocalDateTime.now(),
            level = fail.level,
            details = listOf(
                ApiIncidentResponse.Incident.Detail(
                    code = "${fail.code}/${GlobalProperties.service.id}",
                    description = fail.description,
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
    return this.tryGetStringAttribute("version")
        .bind { version ->
            when (val result = ApiVersion.tryOf(version)) {
                is Result.Success -> result
                is Result.Failure -> result.mapError {
                    DataErrors.Validation.DataFormatMismatch(
                        name = "version",
                        actualValue = version,
                        expectedFormat = "00.00.00"
                    )
                }
            }
        }
}

fun JsonNode.getAction(): Result<Command2Type, DataErrors> {
    return this.getAttribute("action")
        .bind {
            val value = it.asText()
            Command2Type.orNull(value)?.asSuccess<Command2Type, DataErrors>() ?: Result.failure(
                DataErrors.Validation.UnknownValue(
                    name = "action",
                    actualValue = value,
                    expectedValues = Command2Type.allowedValues
                )
            )
        }
}

fun <T : Any> JsonNode.tryParamsToObject(target: Class<T>): Result<T, BadRequestErrors> {
    return tryToObject(target = target)
        .doOnError {
            return Result.failure(
                BadRequestErrors.Parsing(message = "Can not parse 'params'.", request = this.toString())
            )
        }
        .get
        .asSuccess()
}

private fun JsonNode.tryGetStringAttribute(name: String): Result<String, DataErrors> {
    return this.tryGetAttribute(name = name, type = JsonNodeType.STRING)
        .map {
            it.asText()
        }
}

private fun JsonNode.tryGetAttribute(name: String, type: JsonNodeType): Result<JsonNode, DataErrors> =
    getAttribute(name = name)
        .bind { node ->
            if (node.nodeType == type)
                success(node)
            else
                failure(
                    DataErrors.Validation.DataTypeMismatch(
                        name = name,
                        expectedType = type.name,
                        actualType = node.nodeType.name
                    )
                )
        }

private fun asUUID(value: String): Result<UUID, DataErrors> =
    try {
        Result.success<UUID>(UUID.fromString(value))
    } catch (exception: IllegalArgumentException) {
        Result.failure(
            DataErrors.Validation.DataFormatMismatch(
                name = "id",
                expectedFormat = "uuid",
                actualValue = value
            )
        )
    }

fun JsonNode.getAttribute(name: String): Result<JsonNode, DataErrors> {
    return if (has(name)) {
        val attr = get(name)
        if (attr !is NullNode)
            Result.success(attr)
        else
            Result.failure(
                DataErrors.Validation.DataTypeMismatch(name = "$attr", actualType = "null", expectedType = "not null")
            )
    } else
        Result.failure(DataErrors.Validation.MissingRequiredAttribute(name = name))
}

fun JsonNode.tryGetParams(): Result<JsonNode, DataErrors> =
    getAttribute("params")
