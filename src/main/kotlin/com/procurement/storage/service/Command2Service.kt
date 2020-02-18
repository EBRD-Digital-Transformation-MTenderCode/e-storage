package com.procurement.storage.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.infrastructure.handlers.CheckRegistrationHandler
import com.procurement.storage.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.storage.model.dto.bpe.Command2Type
import com.procurement.storage.model.dto.bpe.getAction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class Command2Service(
    val checkRegistrationHandler: CheckRegistrationHandler
) {

    companion object {
        private val log = LoggerFactory.getLogger(Command2Service::class.java)
    }

    fun execute(request: JsonNode): ApiSuccessResponse {

        val response = when (request.getAction()) {
            Command2Type.CHECK_REGISTRATION -> {
                checkRegistrationHandler.handle(request = request)
            }
        }
        if(log.isDebugEnabled)
            log.debug("DataOfResponse: '$response'.")


        return response.also {
            if(log.isDebugEnabled)
                log.debug("RESPONSE CommandService2: '$response'.")
        }
    }
}
