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
import org.springframework.core.io.Resource
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
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

//    @GetMapping(value = ["/get/{fileId}"])
//    fun getFile(@PathVariable(value = "fileId") fileId: String): ResponseEntity<Resource> {
//        val file = storageService.getFileById(fileId)
//        val resource = file.resource
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.parseMediaType("application/octet-stream")
//        headers.contentDisposition = ContentDisposition.parse("attachment; filename=" + file.fileName)
//        headers.contentLength = resource.contentLength()
//        return ResponseEntity(resource, headers, HttpStatus.OK)
//    }

    @GetMapping(value = ["/get/{fileId}"])
    fun getFile(@PathVariable(value = "fileId") fileId: String, response: HttpServletResponse) {
        val fileEntity = storageService.getFileEntityById(fileId)
        response.addHeader("Content-disposition", "attachment; filename=" + fileEntity.fileName)
        response.contentType = "application/octet-stream"
        response.status = HttpStatus.OK.value()
        IOUtils.copyLarge(Files.newInputStream(Paths.get(fileEntity.fileOnServer)), response.outputStream)
        response.flushBuffer()
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(Exception::class)
    fun exception(ex: Exception): ResponseDto {
        return when (ex) {
            is ExternalException -> getExternalExceptionResponseDto(ex)
            else -> getExceptionResponseDto(ex)
        }
    }

}
