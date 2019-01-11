package com.procurement.storage.controller

import com.procurement.storage.exception.ExternalException
import com.procurement.storage.model.dto.bpe.ResponseDto
import com.procurement.storage.model.dto.bpe.getExceptionResponseDto
import com.procurement.storage.model.dto.bpe.getExternalExceptionResponseDto
import com.procurement.storage.model.dto.registration.RegistrationRq
import com.procurement.storage.model.dto.registration.RegistrationRs
import com.procurement.storage.model.dto.registration.UploadRs
import com.procurement.storage.service.StorageService
import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.net.URLEncoder
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping(value = ["/storage"])
class StorageController(private val storageService: StorageService) {

    @PostMapping(value = ["/registration"])
    fun registration(@RequestBody dto: RegistrationRq): ResponseEntity<RegistrationRs> {

        return ResponseEntity(storageService.registerFile(dto), HttpStatus.CREATED)
    }

    @PostMapping(value = ["/upload/{fileId}"], consumes = ["multipart/form-data"])
    fun uploadFile(@PathVariable(value = "fileId") fileId: String,
                   @RequestParam(value = "file") file: MultipartFile): ResponseEntity<UploadRs> {

        return ResponseEntity(storageService.uploadFile(fileId, file), HttpStatus.CREATED)
    }

    @GetMapping(value = ["/get/{fileId}"])
    fun getFileStream(@PathVariable(value = "fileId") fileId: String, response: HttpServletResponse) {

        val fileEntity = storageService.getFileEntityById(fileId)

        val fileInputStream = storageService.getFileStream(fileEntity.fileOnServer!!)

        val encodedFilename: String = URLEncoder.encode(fileEntity.fileName, "UTF-8")


        val attachmentFilename = if(fileEntity.fileName == encodedFilename)
            "filename=\"$encodedFilename\""
        else
            "filename=\"file\"; filename*=utf-8''$encodedFilename"

        response.addHeader("Content-Disposition", "attachment; $attachmentFilename")
        response.contentType = "application/octet-stream"
        response.status = HttpStatus.OK.value()
        IOUtils.copyLarge(fileInputStream, response.outputStream)
        response.flushBuffer()
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseDto {
        return getExceptionResponseDto(ex)
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ExternalException::class)
    fun externalException(ex: ExternalException): ResponseDto {
        return getExternalExceptionResponseDto(ex)
    }
}
