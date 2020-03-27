package com.procurement.storage.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.application.service.Logger
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.util.Action
import com.procurement.storage.domain.util.ValidationResult
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.model.dto.bpe.getVersion
import com.procurement.storage.utils.toJson

abstract class AbstractValidationHandler<ACTION : Action>(
    private val logger: Logger
) : AbstractHandler<ACTION, ApiResponse>(logger) {

    override fun handle(node: JsonNode): ApiResponse {
        val id = node.getId().get
        val version = node.getVersion().get

        return when (val validationResult = execute(node)) {
            is ValidationResult.Ok -> ApiSuccessResponse(version = version, id = id)
                .also {
                    logger.info("The '${action.key}' has been executed. Result: '${toJson(it)}'")
                }
            is ValidationResult.Fail -> responseError(id = id, version = version, fail = validationResult.error)
        }
    }

    abstract fun execute(node: JsonNode): ValidationResult<Fail>
}
