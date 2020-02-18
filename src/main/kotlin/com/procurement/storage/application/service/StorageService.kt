package com.procurement.storage.application.service

import com.procurement.storage.dao.FileDao
import com.procurement.storage.domain.model.document.DocumentId
import com.procurement.storage.exception.BpeErrorException
import com.procurement.storage.exception.ErrorType
import org.springframework.stereotype.Service

interface StorageService {
    fun checkRegistration(requestDocumentIds: List<DocumentId>)
}

@Service
class StorageServiceImpl(private val fileDao: FileDao) : StorageService {

    override fun checkRegistration(requestDocumentIds: List<DocumentId>) {

        val documentIds = requestDocumentIds.asSequence()
            .map { documentId ->
                documentId.toString().also { id ->
                    if (id.isBlank())
                        throw BpeErrorException(
                            error = ErrorType.INVALID_ID,
                            message = "The id = $id in the document is blank"
                        )
                }
            }
            .toSet()
        val dbFiles = fileDao.getAllByIds(documentIds)
        if (dbFiles.isEmpty()) {
            throw BpeErrorException(
                error = ErrorType.FILES_NOT_FOUND,
                message = "Files not found by ids $documentIds"
            )
        }
        val uniqueDbFilesId = dbFiles.asSequence()
            .map { it.id }
            .toSet()
        if (!documentIds.containsAll(uniqueDbFilesId)) {
            throw BpeErrorException(
                error = ErrorType.FILES_NOT_FOUND,
                message = (documentIds - uniqueDbFilesId).toString() + " files not found"
            )
        }
    }
}
