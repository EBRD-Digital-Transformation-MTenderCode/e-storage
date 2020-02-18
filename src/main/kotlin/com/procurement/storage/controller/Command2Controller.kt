package com.procurement.storage.controller

import com.datastax.driver.core.querybuilder.QueryBuilder.toJson
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.config.GlobalProperties
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiVersion
import com.procurement.storage.model.dto.bpe.NaN
import com.procurement.storage.model.dto.bpe.errorResponse
import com.procurement.storage.service.Command2Service
import com.procurement.storage.utils.getBy
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
    private val command2Service:Command2Service
) {

    companion object {
        private val log = LoggerFactory.getLogger(Command2Controller::class.java)
    }

    @PostMapping
    fun command(
        @RequestBody requestBody: String
    ): ResponseEntity<ApiResponse> {
        log.info("Command request ($requestBody).")
        if (log.isDebugEnabled)
            log.debug("RECEIVED COMMAND: '${requestBody}'.")

        val node: JsonNode = try {
            requestBody.toNode()
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            return createErrorResponseEntity(
                expected = expected
            )
        }

        val id = try {
            UUID.fromString(node.getBy(parameter = "id").asText())
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            return createErrorResponseEntity(
                expected = expected
            )
        }

        val version = try {
            ApiVersion.valueOf(node.getBy(parameter = "version").asText())
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            return createErrorResponseEntity(
                id = id,
                expected = expected
            )
        }

        try {
            node.getBy(parameter = "params")
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            return createErrorResponseEntity(
                id = id,
                expected = expected,
                version = version
            )
        }

        val response = try {
            command2Service.execute(request = node)
                .also { response ->
                    if (log.isDebugEnabled)
                        log.debug("RESPONSE (id: '${id}'): '${toJson(response)}'.")
                }
        } catch (expected: Exception) {
            log.debug("Error.", expected)
            return createErrorResponseEntity(
                id = id,
                expected = expected,
                version = version
            )
        }

        return ResponseEntity(response , HttpStatus.OK)
    }

    private fun createErrorResponseEntity(
        expected: Exception,
        id: UUID = NaN,
        version : ApiVersion = GlobalProperties.App.apiVersion
    ): ResponseEntity<ApiResponse> {
        val response = errorResponse(
            exception = expected,
            version = version,
            id = id
        )
        return ResponseEntity(response, HttpStatus.OK)
    }
}
