package com.procurement.storage.controller

import com.datastax.driver.core.querybuilder.QueryBuilder.toJson
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.fail.error.BadRequestErrors
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.model.dto.bpe.errorResponse
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.service.Command2Service
import com.procurement.storage.utils.toNode
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
            .doOnError { error -> return responseEntity(expected = BadRequestErrors.Parsing("Invalid request data")) }
            .get

        val id = node.getId()
            .doOnError { error -> return responseEntity(expected = error) }
            .get

        val response = command2Service.execute(request = node)
            .also { response ->
                if (log.isDebugEnabled)
                    log.debug("RESPONSE (id: '${id}'): '${toJson(response)}'.")
            }
        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun responseEntity(expected: Fail): ResponseEntity<ApiResponse> {
        log.debug("Error.", expected)
        val response = errorResponse(fail = expected)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
