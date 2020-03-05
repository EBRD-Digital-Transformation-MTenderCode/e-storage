package com.procurement.storage.infrastructure.web.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.storage.domain.model.enums.ResponseStatus
import java.time.LocalDateTime
import java.util.*

sealed class ApiResponse(
    @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,

    @field:JsonProperty("version") @param:JsonProperty("version") val version: ApiVersion,

    @field:JsonProperty("result") @param:JsonProperty("result") val result: Any?
) {
    abstract val status: ResponseStatus
}

class ApiSuccessResponse(
    version: ApiVersion,
    id: UUID,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) result: Any? = null
) : ApiResponse(
    version = version,
    result = result,
    id = id
) {
    @field:JsonProperty("status")
    override val status: ResponseStatus = ResponseStatus.SUCCESS
}

class ApiErrorResponse(
    version: ApiVersion,
    id: UUID,
    result: List<Error>
) : ApiResponse(
    version = version,
    result = result,
    id = id
) {
    @field:JsonProperty("status")
    override val status: ResponseStatus = ResponseStatus.ERROR

    class Error(val code: String?, val description: String?)
}

class ApiDataErrorResponse(
    version: ApiVersion,
    id: UUID,
    result: List<Error>
) : ApiResponse(
    version = version,
    result = result,
    id = id
) {
    @field:JsonProperty("status")
    override val status: ResponseStatus = ResponseStatus.ERROR

    class Error(val code: String?, val description: String?, val attributeName: String)
}

class ApiIncidentResponse(
    version: ApiVersion,
    id: UUID,
    result: Incident
) : ApiResponse(
    version = version,
    result = result,
    id = id
) {
    @field:JsonProperty("status")
    override val status: ResponseStatus = ResponseStatus.INCIDENT

    class Incident(val id: UUID, val date: LocalDateTime, val service: Service, val errors: List<Error>) {
        class Service(val id: String, val name: String, val version: String)
        class Error(val code: String, val description: String, val metadata: Any?)
    }
}
