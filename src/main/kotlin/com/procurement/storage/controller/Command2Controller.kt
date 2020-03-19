package com.procurement.storage.controller

import com.datastax.driver.core.querybuilder.QueryBuilder.toJson
import com.procurement.storage.application.service.Logger
import com.procurement.storage.config.GlobalProperties
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.fail.error.BadRequestErrors
import com.procurement.storage.infrastructure.web.dto.ApiResponse
import com.procurement.storage.infrastructure.web.dto.ApiVersion
import com.procurement.storage.model.dto.bpe.NaN
import com.procurement.storage.model.dto.bpe.errorResponse
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.model.dto.bpe.getVersion
import com.procurement.storage.service.Command2Service
import com.procurement.storage.utils.toNode
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

        val version = node.getVersion()
            .doOnError { versionError ->
                val id = node.getId()
                    .doOnError { idError -> return responseEntity(expected = versionError) }
                    .get
                return responseEntity(expected = versionError, id = id)
            }
            .get

        val id = node.getId()
            .doOnError { error -> return responseEntity(expected = error, version = version) }
            .get

        val response = command2Service.execute(request = node)
            .also { response ->
                logger.info("RESPONSE (id: '${id}'): '${toJson(response)}'.")
            }
        return ResponseEntity(response, HttpStatus.OK)
    }

    private fun responseEntity(
        expected: Fail,
        id: UUID = NaN,
        version: ApiVersion = GlobalProperties.App.apiVersion
    ): ResponseEntity<ApiResponse> {
        expected.logging(logger)
        val response = errorResponse(fail = expected, id = id, version = version)
        return ResponseEntity(response, HttpStatus.OK)
    }
}
