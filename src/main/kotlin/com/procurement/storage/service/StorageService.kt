package com.procurement.storage.service

import com.datastax.driver.core.utils.UUIDs
import com.procurement.storage.dao.FileDao
import com.procurement.storage.exception.BpeErrorException
import com.procurement.storage.exception.ErrorType
import com.procurement.storage.exception.ExternalException
import com.procurement.storage.model.dto.bpe.CommandMessage
import com.procurement.storage.model.dto.bpe.ResponseDto
import com.procurement.storage.model.dto.registration.*
import com.procurement.storage.model.entity.FileEntity
import com.procurement.storage.utils.*
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
import java.util.regex.Pattern

@Service
class StorageService(private val fileDao: FileDao) {

    @Value("\${upload.file.path}")
    private var uploadFilePath: String? = null

    @Value("\${upload.file.folder}")
    private var uploadFileFolder: String? = null

    @Value("\${upload.file.extensions}")
    private var fileExtensions: Array<String>? = null

    @Value("\${upload.file.max-weight}")
    private var maxFileWeight: Int? = null

    fun registerFile(dto: RegistrationRq): RegistrationRs {
        checkFileWeight(dto.weight)
        checkFileExtension(dto.fileName)
        val fileEntity = fileDao.save(getEntity(dto))
        return RegistrationRs(data = RegistrationDataRs(
                id = fileEntity.id,
                url = uploadFilePath!! + fileEntity.id,
                dateModified = fileEntity.dateModified?.toLocal(),
                datePublished = fileEntity.datePublished?.toLocal())
        )
    }

    fun uploadFile(fileId: String, file: MultipartFile): UploadRs {
        val fileEntity = fileDao.getOneById(fileId)
        if (fileEntity != null) {
            checkFileName(fileEntity, file)
            checkFileSize(fileEntity, file)
            checkFileHash(fileEntity, file)
            fileEntity.fileOnServer = writeFileToDisk(fileEntity, file)
            fileDao.save(fileEntity)
            return UploadRs(data = UploadDataRs(
                    id = fileEntity.id,
                    url = uploadFilePath!! + fileEntity.id,
                    dateModified = fileEntity.dateModified?.toLocal(),
                    datePublished = fileEntity.datePublished?.toLocal())
            )
        } else
            throw ExternalException(ErrorType.FILE_NOT_FOUND)
    }

    fun setPublishDateBatch(cm: CommandMessage): ResponseDto {
        val datePublished = cm.context.startDate.toLocal()
        val dto = toObject(DocumentsRq::class.java, cm.data)
        for (document in dto.documents) {
            publish(document, datePublished)
        }
        return ResponseDto(data = dto)
    }

    fun validateDocuments(cm: CommandMessage): ResponseDto {
        val dto = toObject(DocumentsRq::class.java, cm.data)
        val documentsDto = dto.documents
        val docIdsSet = documentsDto.asSequence().map { it.id }.toHashSet()
        if (docIdsSet.size != documentsDto.size) throw  BpeErrorException(ErrorType.INVALID_ID)
        for (document in documentsDto) {
            validate(document)
        }
        return ResponseDto(data = dto)
    }

    fun getFileById(fileId: String): FileDataRs {
        val fileEntity = fileDao.getOneById(fileId)
        if (fileEntity != null)
            return if (fileEntity.isOpen) {
                if (fileEntity.fileOnServer == null) {
                    throw ExternalException(ErrorType.NO_FILE_ON_SERVER)
                }
                FileDataRs(fileEntity.fileName, readFileFromDisk(fileEntity.fileOnServer))
            } else {
                throw ExternalException(ErrorType.FILE_IS_CLOSED)
            }
        else
            throw ExternalException(ErrorType.FILE_NOT_FOUND)
    }

    private fun publish(document: Document, datePublished: LocalDateTime) {
        val fileEntity = fileDao.getOneById(document.id)
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
            throw BpeErrorException(ErrorType.FILE_NOT_FOUND)
        }
    }

    private fun validate(document: Document) {
        fileDao.getOneById(document.id) ?: throw  BpeErrorException(ErrorType.FILE_NOT_FOUND)
    }

    private fun checkFileWeight(fileWeight: Long) {
        if (fileWeight == 0L || maxFileWeight!! < fileWeight) throw ExternalException(ErrorType.INVALID_SIZE)
    }

    private fun checkFileExtension(fileName: String) {
        val fileExtension: String = FilenameUtils.getExtension(fileName)
        if (fileExtension !in fileExtensions!!) throw ExternalException(ErrorType.INVALID_EXTENSION)
    }

    private fun checkFileHash(fileEntity: FileEntity, file: MultipartFile) {
        try {
            val uploadFileHash = DigestUtils.md5DigestAsHex(file.inputStream).toUpperCase()
            if (uploadFileHash != fileEntity.hash) {
                throw ExternalException(ErrorType.INVALID_HASH)
            }
        } catch (e: IOException) {
            throw ExternalException(ErrorType.READ_EXCEPTION)
        }
    }

    private fun checkFileName(fileEntity: FileEntity, file: MultipartFile) {
        if (file.originalFilename != fileEntity.fileName)
            throw ExternalException(ErrorType.INVALID_NAME)
    }

    private fun checkFileSize(fileEntity: FileEntity, file: MultipartFile) {
        val fileSizeMb = file.size
        if (fileSizeMb > fileEntity.weight)
            throw ExternalException(ErrorType.INVALID_SIZE)
    }

    private fun writeFileToDisk(fileEntity: FileEntity, file: MultipartFile): String {
        try {
            val fileName = file.originalFilename
            if (file.isEmpty) throw ExternalException(ErrorType.EMPTY_FILE, fileName!!)
            if (fileName!!.contains(".."))
                throw ExternalException(ErrorType.INVALID_PATH, fileName)
            val fileID = fileEntity.id
            val dir = uploadFileFolder + "/" + fileID.substring(0, 2) + "/" + fileID.substring(2, 4) + "/"
            Files.createDirectories(Paths.get(dir))
            val url = dir + fileID
            val targetFile = File(url)
            FileUtils.copyInputStreamToFile(file.inputStream, targetFile)
            return url
        } catch (e: IOException) {
            throw ExternalException(ErrorType.WRITE_EXCEPTION, e.message!!)
        }

    }

    private fun readFileFromDisk(fileOnServer: String?): ByteArrayResource {
        try {
            val path = Paths.get(fileOnServer)
            return ByteArrayResource(Files.readAllBytes(path))
        } catch (e: IOException) {
            throw ExternalException(ErrorType.READ_EXCEPTION, e.message!!)
        }

    }

    private fun getEntity(dto: RegistrationRq): FileEntity {
        val fileId = if (dto.id != null) {
            val id = dto.id.substring(0, 36)
            val p = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")
            if (!p.matcher(id).matches()) throw ExternalException(ErrorType.INVALID_FILE_ID)
            id + "-" + milliNowUTC()
        } else {
            UUIDs.random().toString() + "-" + milliNowUTC()
        }
        return FileEntity(
                id = fileId,
                isOpen = false,
                dateModified = nowUTC().toDate(),
                hash = dto.hash.toUpperCase(),
                weight = dto.weight,
                fileName = dto.fileName,
                datePublished = null,
                fileOnServer = null,
                owner = null)
    }
}
