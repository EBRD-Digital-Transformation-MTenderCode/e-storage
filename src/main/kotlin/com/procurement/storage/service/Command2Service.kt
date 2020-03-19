package com.procurement.storage.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.infrastructure.handler.check.registration.CheckRegistrationHandler
import com.procurement.storage.infrastructure.handler.open.OpenAccessHandler
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.model.dto.bpe.Command2Type
import com.procurement.storage.model.dto.bpe.errorResponse
import com.procurement.storage.model.dto.bpe.getAction
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.model.dto.bpe.getVersion
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class Command2Service(
    val checkRegistrationHandler: CheckRegistrationHandler,
    val openAccessHandler: OpenAccessHandler
) {

    companion object {
        private val log = LoggerFactory.getLogger(Command2Service::class.java)
    }

    fun execute(request: JsonNode): ApiResponse {

        val version = request.getVersion()
            .doOnError { versionError ->
                val id = request.getId()
                    .doOnError { idError -> return errorResponse(fail = versionError) }
                    .get
                return errorResponse(fail = versionError, id = id)
            }
            .get

        val id = request.getId()
            .doOnError { error -> return errorResponse(fail = error, version = version) }
            .get

        val action = request.getAction()
            .doOnError { error -> return errorResponse(id = id, version = version, fail = error) }
            .get

        val response = when (action) {
            Command2Type.CHECK_REGISTRATION -> checkRegistrationHandler.handle(node = request)
            Command2Type.OPEN_ACCESS -> openAccessHandler.handle(node = request)
        }

        if (log.isDebugEnabled)
            log.debug("DataOfResponse: '$response'.")

        return response
    }
}
