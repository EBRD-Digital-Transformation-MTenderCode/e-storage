package com.procurement.storage.service

import com.datastax.driver.core.utils.UUIDs
import com.procurement.storage.dao.FileDao
import com.procurement.storage.exception.*
import com.procurement.storage.model.dto.bpe.CommandMessage
import com.procurement.storage.model.dto.bpe.ResponseDto
import com.procurement.storage.model.dto.registration.*
import com.procurement.storage.model.entity.FileEntity
import com.procurement.storage.utils.nowUTC
import com.procurement.storage.utils.toDate
import com.procurement.storage.utils.toLocal
import com.procurement.storage.utils.toObject
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

    fun registerFile(dto: RegistrationRq): ResponseDto

    fun uploadFile(fileId: String, file: MultipartFile): ResponseDto

    fun getFileById(fileId: String): FileDataRs

    fun setPublishDateBatch(cm: CommandMessage): ResponseDto

    fun validateDocuments(cm: CommandMessage): ResponseDto
}

@Service
class StorageServiceImpl(private val fileDao: FileDao) : StorageService {

    @Value("\${upload.file.path}")
    private var uploadFilePath: String? = null

    @Value("\${upload.file.folder}")
    private var uploadFileFolder: String? = null

    @Value("\${upload.file.extensions}")
    private var fileExtensions: Array<String>? = null

    @Value("\${upload.file.max-weight}")
    private var maxFileWeight: Int? = null

    override fun registerFile(dto: RegistrationRq): ResponseDto {
        checkFileWeight(dto.weight)
        checkFileExtension(dto.fileName)
        val fileEntity = fileDao.save(getEntity(dto))
        return getResponseDto(fileEntity)
    }

    override fun uploadFile(fileId: String, file: MultipartFile): ResponseDto {
        val fileEntity = fileDao.getOneById(UUID.fromString(fileId))
        if (fileEntity != null) {
            checkFileName(fileEntity, file)
            checkFileSize(fileEntity, file)
            checkFileHash(fileEntity, file)
            fileEntity.fileOnServer = writeFileToDisk(fileEntity, file)
            fileDao.save(fileEntity)
            return getResponseDto(fileEntity)
        } else
            throw UploadFileValidationException("File not found.")
    }

    override fun setPublishDateBatch(cm: CommandMessage): ResponseDto {
        val datePublished = cm.context.startDate.toLocal()
        val dto = toObject(DocumentsRq::class.java, cm.data)
        for (document in dto.documents) {
            publish(document, datePublished)
        }
        return ResponseDto(data = dto)
    }

    override fun validateDocuments(cm: CommandMessage): ResponseDto {
        val dto = toObject(DocumentsRq::class.java, cm.data)
        for (document in dto.documents) {
            validate(document)
        }
        return ResponseDto(data = dto)
    }

    override fun getFileById(fileId: String): FileDataRs {
        val fileEntity = fileDao.getOneById(UUID.fromString(fileId))
        if (fileEntity != null)
            return if (fileEntity.isOpen) {
                if (fileEntity.fileOnServer == null) {
                    throw GetFileException("No file on server.")
                }
                FileDataRs(fileEntity.fileName, readFileFromDisk(fileEntity.fileOnServer))
            } else {
                throw GetFileException("File is closed.")
            }
        else throw GetFileException("File not found.")
    }

    fun publish(document: Document, datePublished: LocalDateTime) {
        val fileEntity = fileDao.getOneById(UUID.fromString(document.id))
        if (fileEntity != null) {
            if (!fileEntity.isOpen) {
                fileEntity.datePublished = datePublished.toDate()
                fileEntity.isOpen = true
                fileDao.save(fileEntity)
                document.datePublished = datePublished
                document.url = uploadFilePath + document.id
            } else {
                document.datePublished = fileEntity.datePublished?.toLocal()
                document.url = uploadFilePath + document.id
            }
        } else {
            throw ErrorException(ErrorType.DATA_NOT_FOUND)
        }
    }

    fun validate(document: Document) {
        fileDao.getOneById(UUID.fromString(document.id)) ?: throw  ErrorException(ErrorType.DATA_NOT_FOUND)
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
            throw UploadFileValidationException("File read exception.")
        }
    }

    private fun checkFileName(fileEntity: FileEntity, file: MultipartFile) {
        if (file.originalFilename != fileEntity.fileName)
            throw UploadFileValidationException("Invalid file name.")
    }

    private fun checkFileSize(fileEntity: FileEntity, file: MultipartFile) {
        val fileSizeMb = file.size
        if (fileSizeMb > fileEntity.weight)
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

    private fun getEntity(dto: RegistrationRq): FileEntity {
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

    private fun getResponseDto(entity: FileEntity): ResponseDto {
        return ResponseDto(data = DataRs(
                id = entity.id.toString(),
                url = uploadFilePath!! + entity.id.toString(),
                dateModified = entity.dateModified?.toLocal(),
                datePublished = entity.datePublished?.toLocal())
        )

    }
}
