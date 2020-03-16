package com.procurement.storage.controller

import com.datastax.driver.core.querybuilder.QueryBuilder.toJson
import com.procurement.storage.application.service.Logger
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.fail.error.BadRequestErrors
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.model.dto.bpe.errorResponse
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.service.Command2Service
import com.procurement.storage.utils.toNode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/command2")
class Command2Controller(
    private val command2Service: Command2Service,
    private val logger: Logger
) {

    @PostMapping
    fun command(@RequestBody requestBody: String): ResponseEntity<ApiResponse> {

        logger.info("RECEIVED COMMAND: '${requestBody}'.")

        val node = requestBody.toNode()
            .doOnError { error ->
                return responseEntity(
                    expected = BadRequestErrors.Parsing(
                        message = "Invalid request data",
                        request = requestBody
                    )
                )
            }
            .get

        val id = node.getId()
            .doOnError { error -> return responseEntity(expected = error) }
            .get

        val response = command2Service.execute(request = node)
            .also { response ->
                logger.info("RESPONSE (id: '${id}'): '${toJson(response)}'.")
            }
        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun responseEntity(expected: Fail): ResponseEntity<ApiResponse> {
        expected.logging(logger)
        val response = errorResponse(fail = expected)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
