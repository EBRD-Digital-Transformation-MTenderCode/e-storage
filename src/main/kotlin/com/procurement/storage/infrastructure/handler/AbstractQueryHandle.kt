package com.procurement.storage.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.util.Action
import com.procurement.storage.domain.util.Result
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.model.dto.bpe.getVersion
import org.slf4j.LoggerFactory

abstract class AbstractQueryHandler<ACTION : Action, R : Any> : AbstractHandler<ACTION, ApiResponse>() {
    companion object {
        private val log = LoggerFactory.getLogger(AbstractQueryHandler::class.java)
    }

    override fun handle(node: JsonNode): ApiResponse {
        val id = node.getId().get
        val version = node.getVersion().get

        val result = execute(node)
            .also {
                log.debug("The '{}' has been executed. Result: '{}'", action.key, it)
            }

        return when (result) {
            is Result.Success -> ApiSuccessResponse(id = id, version = version, result = result.get)
            is Result.Failure -> responseError(id = id, version = version, fails = result.error)
        }
    }

    abstract fun execute(node: JsonNode): Result<R, List<Fail>>
}
