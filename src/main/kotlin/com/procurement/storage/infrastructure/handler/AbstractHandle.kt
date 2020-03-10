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
import com.procurement.storage.model.dto.bpe.generateIncident
import java.util.*

abstract class AbstractHandler<ACTION : Action, R : Any> : Handler<ACTION, ApiResponse> {

    protected fun responseError(id: UUID, version: ApiVersion, fail: Fail): ApiResponse =
        when (fail) {
            is Fail.Error -> {
                when (fail) {
                    is DataErrors.Validation -> {
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
                    }
                    else -> {
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
                    }
                }
            }
            is Fail.Incident -> {
                when (fail) {
                    is Fail.Incident.Parsing -> {
                        val incident = Fail.Incident.DatabaseIncident()
                        ApiIncidentResponse(
                            id = id,
                            version = version,
                            result = generateIncident(fail = incident)
                        )
                    }
                    else -> ApiIncidentResponse(
                        id = id,
                        version = version,
                        result = generateIncident(fail = fail)
                    )
                }
            }
        }


}
