package com.procurement.storage.controller

import com.procurement.storage.exception.BpeErrorException
import com.procurement.storage.model.dto.bpe.CommandMessage
import com.procurement.storage.model.dto.bpe.CommandType
import com.procurement.storage.model.dto.bpe.ResponseDto
import com.procurement.storage.model.dto.bpe.getErrorExceptionResponseDto
import com.procurement.storage.model.dto.bpe.getExceptionResponseDto
import com.procurement.storage.service.StorageService
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/command")
class CommandController(private val storageService: StorageService) {

    companion object {
        private val log = LoggerFactory.getLogger(CommandController::class.java)
    }

    @PostMapping
    fun command(@RequestBody commandMessage: CommandMessage): ResponseEntity<ResponseDto> {
        log.info("Command request ($commandMessage).")
        return ResponseEntity(execute(commandMessage), HttpStatus.OK)
    }

    fun execute(cm: CommandMessage): ResponseDto {
        return when (cm.command) {
            CommandType.VALIDATE -> storageService.validateDocumentsBatch(cm)
            CommandType.PUBLISH -> storageService.setPublishDateBatch(cm)
        }
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseDto {
        log.error("Error of the processing command.", ex)
        return when (ex) {
            is BpeErrorException -> getErrorExceptionResponseDto(ex)
            else -> getExceptionResponseDto(ex)
        }
    }
}



