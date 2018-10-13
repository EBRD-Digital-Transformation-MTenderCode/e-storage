package com.procurement.storage.controller

import com.procurement.storage.exception.GetFileException
import com.procurement.storage.exception.RegistrationValidationException
import com.procurement.storage.exception.UploadFileValidationException
import com.procurement.storage.model.dto.bpe.ResponseDto
import com.procurement.storage.model.dto.registration.RegistrationRq
import com.procurement.storage.model.dto.registration.RegistrationRs
import com.procurement.storage.model.dto.registration.UploadRs
import com.procurement.storage.service.StorageService
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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
    fun getFile(@PathVariable(value = "fileId") fileId: String): ResponseEntity<Resource> {
        val file = storageService.getFileById(fileId)
        val resource = file.resource
        val headers = HttpHeaders()
        headers.contentType = MediaType.parseMediaType("application/octet-stream")
        headers.contentDisposition = ContentDisposition.parse("attachment; filename=" + file.fileName)
        headers.contentLength = resource.contentLength()
        return ResponseEntity(resource, headers, HttpStatus.OK)
    }

    @ResponseBody
    @ExceptionHandler(RegistrationValidationException::class)
    fun registrationValidation(e: RegistrationValidationException) = ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ResponseBody
    @ExceptionHandler(UploadFileValidationException::class)
    fun uploadFileValidation(e: UploadFileValidationException) = ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ResponseBody
    @ExceptionHandler(GetFileException::class)
    fun getFile(e: GetFileException) = ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
}
