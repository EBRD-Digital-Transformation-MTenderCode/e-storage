package com.procurement.storage.infrastructure.handler

import com.procurement.storage.config.GlobalProperties
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.fail.error.DataErrors
import com.procurement.storage.domain.util.Action
import com.procurement.storage.infrastructure.web.dto.ApiDataErrorResponse
import com.procurement.storage.infrastructure.web.dto.ApiErrorResponse
import com.procurement.storage.infrastructure.web.dto.ApiIncidentResponse
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiVersion
import java.time.LocalDateTime
import java.util.*

abstract class AbstractHandler<ACTION : Action, R : Any> : Handler<ACTION, ApiResponse> {

    protected fun responseError(id: UUID, version: ApiVersion, fails: List<Fail>): ApiResponse {
        return when (fails[0]) {
            is DataErrors -> {
                fails as List<DataErrors.Validation>
                ApiDataErrorResponse(
                    version = version,
                    id = id,
                    result = fails.map { fail ->
                        ApiDataErrorResponse.Error(
                            code = "${fail.code}/${GlobalProperties.service.id}",
                            description = fail.description,
                            attributeName = fail.name
                        )
                    }
                )
            }
            is Fail.Error -> {
                fails as List<Fail.Error>
                ApiErrorResponse(
                    version = version,
                    id = id,
                    result = fails.map { fail ->
                        ApiErrorResponse.Error(
                            code = "${fail.code}/${GlobalProperties.service.id}",
                            description = fail.description
                        )
                    }
                )
            }
            is Fail.Incident -> {
                fails as List<Fail.Incident>
                ApiIncidentResponse(
                    id = id,
                    version = version,
                    result = ApiIncidentResponse.Incident(
                        id = UUID.randomUUID(),
                        date = LocalDateTime.now(),
                        errors = fails.map { fail ->
                            ApiIncidentResponse.Incident.Error(
                                code = "${fail.code}/${GlobalProperties.service.id}",
                                description = fail.description,
                                metadata = null
                            )
                        },
                        service = ApiIncidentResponse.Incident.Service(
                            id = GlobalProperties.service.id,
                            version = GlobalProperties.service.version,
                            name = GlobalProperties.service.name
                        )
                    )
                )
            }
        }
    }
}
