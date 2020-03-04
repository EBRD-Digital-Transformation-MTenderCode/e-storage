package com.procurement.storage.controller

import com.datastax.driver.core.querybuilder.QueryBuilder.toJson
import com.procurement.storage.config.GlobalProperties
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiVersion
import com.procurement.storage.model.dto.bpe.NaN
import com.procurement.storage.model.dto.bpe.errorResponse
import com.procurement.storage.model.dto.bpe.getAction
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.model.dto.bpe.getVersion
import com.procurement.storage.model.dto.bpe.hasParams
import com.procurement.storage.service.Command2Service
import com.procurement.storage.utils.toNode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/command2")
class Command2Controller(
    private val command2Service: Command2Service
) {

    companion object {
        private val log = LoggerFactory.getLogger(Command2Controller::class.java)
    }

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse> {
        if (log.isDebugEnabled)
            log.debug("RECEIVED COMMAND: '${requestBody}'.")

        val node = requestBody.toNode()
            .doOnError { error -> return createErrorResponseEntity(expected = error) }
            .get

        val id = node.getId()
            .doOnError { error -> return createErrorResponseEntity(expected = error) }
            .get

        val version = node.getVersion()
            .doOnError { error -> return createErrorResponseEntity(id = id, expected = error) }
            .get

        node.getAction()
            .doOnError { error -> return createErrorResponseEntity(id = id, expected = error, version = version) }

        val hasParams = node.hasParams()
        if (hasParams.isError)
            return createErrorResponseEntity(id = id, expected = hasParams.error, version = version)

        val response = command2Service.execute(request = node)
            .also { response ->
                if (log.isDebugEnabled)
                    log.debug("RESPONSE (id: '${id}'): '${toJson(response)}'.")
            }
        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun createErrorResponseEntity(
        expected: Fail,
        id: UUID = NaN,
        version: ApiVersion = GlobalProperties.App.apiVersion
    ): ResponseEntity<ApiResponse> {
        log.debug("Error.", expected)
        val response = errorResponse(exception = expected, version = version, id = id)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
