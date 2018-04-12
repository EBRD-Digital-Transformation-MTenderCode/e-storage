package com.procurement.storage.controller

import com.fasterxml.jackson.databind.JsonMappingException
import com.procurement.storage.exception.GetFileException
import com.procurement.storage.exception.PublishFileException
import com.procurement.storage.exception.RegistrationValidationException
import com.procurement.storage.exception.UploadFileValidationException
import com.procurement.storage.model.dto.bpe.ResponseDetailsDto
import com.procurement.storage.model.dto.bpe.ResponseDto
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.OK
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import javax.servlet.ServletException
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ControllerExceptionHandler {

    @ResponseBody
    @ExceptionHandler(RegistrationValidationException::class)
    fun registrationValidation(e: RegistrationValidationException) = ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ResponseBody
    @ExceptionHandler(UploadFileValidationException::class)
    fun uploadFileValidation(e: UploadFileValidationException) = ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ResponseBody
    @ExceptionHandler(GetFileException::class)
    fun getFile(e: GetFileException) = ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(PublishFileException::class)
    fun publishFile(e: PublishFileException) =
            ResponseDto(false, getErrors(e.javaClass.name, e.message), null)

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(NullPointerException::class)
    fun nullPointer(e: NullPointerException) =
            ResponseDto(false, getErrors(e.javaClass.name, e.message), null)

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValid(e: MethodArgumentNotValidException) =
            ResponseDto(false, getErrors(e.bindingResult), null)

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolation(e: ConstraintViolationException) =
            ResponseDto(false, getErrors(e), null)

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(JsonMappingException::class)
    fun jsonMapping(e: JsonMappingException) =
            ResponseDto(false, getErrors(e.javaClass.name, e.message), null)

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(IllegalArgumentException::class)
    fun illegalArgument(e: IllegalArgumentException) =
            ResponseDto(false, getErrors(e.javaClass.name, e.message), null)


    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun methodArgumentTypeMismatch(e: MethodArgumentTypeMismatchException) =
            ResponseDto(false, getErrors(e.javaClass.name, e.message), null)

    @ResponseBody
    @ResponseStatus(OK)
    @ExceptionHandler(ServletException::class)
    fun servlet(e: ServletException) =
            ResponseDto(false, getErrors(e.javaClass.name, e.message), null)

    private fun getErrors(result: BindingResult) =
            result.fieldErrors.asSequence()
                    .map { ResponseDetailsDto(code = it.field, message = """${it.code} : ${it.defaultMessage}""") }
                    .toList()

    private fun getErrors(e: ConstraintViolationException) =
            e.constraintViolations.asSequence()
                    .map {
                        ResponseDetailsDto(
                                code = it.propertyPath.toString(),
                                message = """${it.message} ${it.messageTemplate}""")
                    }
                    .toList()


    private fun getErrors(code: String, error: String?) = listOf(ResponseDetailsDto(code = code, message = error))
}

