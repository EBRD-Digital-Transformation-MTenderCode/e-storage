package com.procurement.storage.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.util.Action
import com.procurement.storage.domain.util.ValidationResult
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.model.dto.bpe.getVersion
import org.slf4j.LoggerFactory

abstract class AbstractValidationHandler<ACTION : Action> : AbstractHandler<ACTION, ApiResponse>() {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractValidationHandler::class.java)
    }

    override fun handle(node: JsonNode): ApiResponse {
        val id = node.getId().get
        val version = node.getVersion().get

        val validationResult = execute(node)
            .also {
                log.debug("The '{}' has been executed. Result: '{}'", action.key, it)
            }

        return when (validationResult) {
            is ValidationResult.Ok -> ApiSuccessResponse(version = version, id = id)
            is ValidationResult.Fail -> responseError(id = id, version = version, fails = validationResult.error)
        }
    }

    abstract fun execute(node: JsonNode): ValidationResult<List<Fail>>
}
