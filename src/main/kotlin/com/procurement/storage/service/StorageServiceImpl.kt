package com.procurement.storage.service

import com.datastax.driver.core.utils.UUIDs
import com.procurement.storage.exception.GetFileException
import com.procurement.storage.exception.PublishFileException
import com.procurement.storage.exception.RegistrationValidationException
import com.procurement.storage.exception.UploadFileValidationException
import com.procurement.storage.model.dto.bpe.ResponseDto
import com.procurement.storage.model.dto.registration.*
import com.procurement.storage.model.entity.FileEntity
import com.procurement.storage.repository.FileRepository
import com.procurement.storage.utils.nowUTC
import com.procurement.storage.utils.toDate
import com.procurement.storage.utils.toLocal
import liquibase.util.file.FilenameUtils
import org.apache.commons.io.FileUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ByteArrayResource
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import java.util.*

interface StorageService {

    fun registerFile(dto: RegistrationRequestDto): ResponseDto<*>

    fun uploadFile(fileId: String, file: MultipartFile): ResponseDto<*>

    fun setPublishDateBatch(datePublished: LocalDateTime, dto: DocumentsRequestDto): ResponseDto<*>

    fun getFileById(fileId: String): FileData
}

@Service
class StorageServiceImpl(private val fileRepository: FileRepository) : StorageService {

    @Value("\${upload.file.path}")
    private var uploadFilePath: String? = null

    @Value("\${upload.file.folder}")
    private var uploadFileFolder: String? = null

    @Value("\${upload.file.extensions}")
    private var fileExtensions: Array<String>? = null

    @Value("\${upload.file.max-weight}")
    private var maxFileWeight: Int? = null

    override fun registerFile(dto: RegistrationRequestDto): ResponseDto<Any> {
        checkFileWeight(dto.weight)
        checkFileExtension(dto.fileName)
        val fileEntity = fileRepository.save(getEntity(dto))
        return getResponseDto(fileEntity)
    }

    override fun uploadFile(fileId: String, file: MultipartFile): ResponseDto<Any> {
        val fileEntity = fileRepository.getOneById(UUID.fromString(fileId))
        if (fileEntity != null) {
            checkFileName(fileEntity, file)
            checkFileSize(fileEntity, file)
            checkFileHash(fileEntity, file)
            fileEntity.fileOnServer = writeFileToDisk(fileEntity, file)
            fileRepository.save(fileEntity)
            return getResponseDto(fileEntity)
        } else
            throw UploadFileValidationException("FileData not found.")
    }

    override fun setPublishDateBatch(datePublished: LocalDateTime, dto: DocumentsRequestDto): ResponseDto<DocumentsRequestDto> {
        for (document in dto.documents) {
            publish(document, datePublished)
        }
        return ResponseDto(success = true, details = null, data = dto)
    }

    override fun getFileById(fileId: String): FileData {
        val fileEntity = fileRepository.getOneById(UUID.fromString(fileId))
        if (fileEntity != null)
            return if (fileEntity.isOpen) {
                FileData(fileEntity.fileName!!, readFileFromDisk(fileEntity.fileOnServer))
            } else {
                throw GetFileException("FileData is closed.")
            }
        else throw GetFileException("FileData not found.")
    }

    fun publish(document: DocumentDto, datePublished: LocalDateTime) {
        val fileEntity = fileRepository.getOneById(UUID.fromString(document.id))
        if (fileEntity != null) {
            if (!fileEntity.isOpen) {
                fileEntity.datePublished = datePublished.toDate()
                fileEntity.isOpen = true
                fileRepository.save(fileEntity)
                document.datePublished = datePublished
                document.url = uploadFilePath + document.id
            } else {
                document.datePublished = fileEntity.datePublished?.toLocal()
                document.url = uploadFilePath + document.id
            }
        } else {
            throw PublishFileException("FileData not found by id: " + document.id)
        }
    }

    private fun checkFileWeight(fileWeight: Long) {
        if (fileWeight == 0L || maxFileWeight!! < fileWeight)
            throw RegistrationValidationException("Invalid file size for registration.")
    }

    private fun checkFileExtension(fileName: String) {
        val fileExtension: String = FilenameUtils.getExtension(fileName)
        if (fileExtension !in fileExtensions!!)
            throw RegistrationValidationException("Invalid file extension for registration.")
    }

    private fun checkFileHash(fileEntity: FileEntity, file: MultipartFile) {
        try {
            val uploadFileHash = DigestUtils.md5DigestAsHex(file.inputStream).toUpperCase()
            if (uploadFileHash != fileEntity.hash) {
                throw UploadFileValidationException("Invalid file hash.")
            }
        } catch (e: IOException) {
            throw UploadFileValidationException("FileData read exception.")
        }
    }

    private fun checkFileName(fileEntity: FileEntity, file: MultipartFile) {
        if (file.originalFilename != fileEntity.fileName)
            throw UploadFileValidationException("Invalid file name.")
    }

    private fun checkFileSize(fileEntity: FileEntity, file: MultipartFile) {
        val fileSizeMb = file.size
        if (fileSizeMb > fileEntity.weight!!)
            throw UploadFileValidationException("Invalid file size.")
    }

    private fun writeFileToDisk(fileEntity: FileEntity, file: MultipartFile): String {
        try {
            val fileName = file.originalFilename
            if (file.isEmpty) throw UploadFileValidationException("Failed to store empty file " + fileName!!)
            if (fileName!!.contains(".."))
                throw UploadFileValidationException("Cannot store file with relative path outside current directory.$fileName")
            val fileID = fileEntity.id.toString()
            val dir = uploadFileFolder + "/" + fileID.substring(0, 2) + "/" + fileID.substring(2, 4) + "/"
            Files.createDirectories(Paths.get(dir))
            val url = dir + fileID
            val targetFile = File(url)
            FileUtils.copyInputStreamToFile(file.inputStream, targetFile)
            return url
        } catch (e: IOException) {
            throw UploadFileValidationException(e.message!!)
        }

    }

    private fun readFileFromDisk(fileOnServer: String?): ByteArrayResource {
        try {
            val path = Paths.get(fileOnServer)
            return ByteArrayResource(Files.readAllBytes(path))
        } catch (e: IOException) {
            throw GetFileException(e.message!!)
        }

    }

    private fun getEntity(dto: RegistrationRequestDto): FileEntity {
        return FileEntity(
                id = UUIDs.timeBased(),
                isOpen = false,
                dateModified = nowUTC().toDate(),
                hash = dto.hash.toUpperCase(),
                weight = dto.weight,
                fileName = dto.fileName,
                datePublished = null,
                fileOnServer = null,
                owner = null)
    }

    private fun getResponseDto(entity: FileEntity): ResponseDto<DataDto> {
        return ResponseDto(
                success = true,
                details = null,
                data = DataDto(
                        id = entity.id.toString(),
                        url = uploadFilePath!! + entity.id.toString(),
                        dateModified = entity.dateModified?.toLocal(),
                        datePublished = entity.datePublished?.toLocal())
        )
    }
}
