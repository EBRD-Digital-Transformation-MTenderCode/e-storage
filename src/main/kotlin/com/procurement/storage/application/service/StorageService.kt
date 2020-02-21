package com.procurement.storage.application.service

import com.procurement.storage.config.UploadFileProperties
import com.procurement.storage.dao.FileDao
import com.procurement.storage.domain.model.document.DocumentId
import com.procurement.storage.exception.BpeErrorException
import com.procurement.storage.exception.ErrorType
import com.procurement.storage.infrastructure.dto.OpenAccessResponse
import com.procurement.storage.utils.toLocal
import org.springframework.stereotype.Service

interface StorageService {
    fun checkRegistration(requestDocumentIds: List<DocumentId>)
    fun openAccess(requestDocumentIds: List<DocumentId>): List<OpenAccessResponse>
}

@Service
class StorageServiceImpl(
    private val fileDao: FileDao,
    private val uploadFileProperties: UploadFileProperties
) : StorageService {

    override fun openAccess(requestDocumentIds: List<DocumentId>): List<OpenAccessResponse> {
        val documentIds = mapRequestDocumentIdsToString(documentIds = requestDocumentIds)
        val dbFiles = getDocumentsByIds(documentIds)

        if (dbFiles.isEmpty()) {
            throw BpeErrorException(
                error = ErrorType.FILES_NOT_FOUND,
                message = "Files not found by ids $documentIds"
            )
        }
        return dbFiles.map { file ->
            OpenAccessResponse(
                id = DocumentId.fromString(file.id),
                datePublished = file.datePublished!!.toLocal(),
                uri = uploadFileProperties.path + file.id
            )
        }
    }

    override fun checkRegistration(requestDocumentIds: List<DocumentId>) {

        val documentIds = mapRequestDocumentIdsToString(documentIds = requestDocumentIds)
        val dbFiles = getDocumentsByIds(documentIds)

        if (dbFiles.isEmpty()) {
            throw BpeErrorException(
                error = ErrorType.FILES_NOT_FOUND,
                message = "Files not found by ids $documentIds"
            )
        }
        val uniqueDbFilesId = dbFiles.asSequence()
            .map { it.id }
            .toSet()
        if (uniqueDbFilesId != documentIds) {
            throw BpeErrorException(
                error = ErrorType.FILES_NOT_FOUND,
                message = (documentIds - uniqueDbFilesId).toString() + " files not found"
            )
        }
    }

    private fun getDocumentsByIds(documentIds: Set<String>) = fileDao.getAllByIds(documentIds)

    private fun mapRequestDocumentIdsToString(documentIds: List<DocumentId>): Set<String> {
        return documentIds.asSequence()
            .map { documentId ->
                documentId.toString().also { id ->
                    checkIfIdIsBlank(id = id)
                }
            }
            .toSet()
    }

    private fun checkIfIdIsBlank(id: String) {
        if (id.isBlank())
            throw BpeErrorException(
                error = ErrorType.INVALID_ID,
                message = "The id = $id in the document is blank"
            )
    }
}
