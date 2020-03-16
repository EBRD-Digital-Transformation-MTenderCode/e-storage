package com.procurement.storage.application.service

import com.procurement.storage.application.repository.FileRepository
import com.procurement.storage.config.UploadFileProperties
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.fail.error.ValidationErrors
import com.procurement.storage.domain.model.document.DocumentId
import com.procurement.storage.domain.util.Result
import com.procurement.storage.domain.util.ValidationResult
import com.procurement.storage.domain.util.ValidationRule
import com.procurement.storage.domain.util.extension.getUnknownElements
import com.procurement.storage.domain.util.extension.mapResult
import com.procurement.storage.domain.util.extension.toSetBy
import com.procurement.storage.domain.util.validate
import com.procurement.storage.infrastructure.dto.OpenAccessResult
import com.procurement.storage.utils.toLocal
import org.springframework.stereotype.Service

interface StorageService {
    fun checkRegistration(requestDocumentIds: List<DocumentId>): ValidationResult<Fail>
    fun openAccess(requestDocumentIds: List<DocumentId>): Result<List<OpenAccessResult>, Fail>
}

@Service
class StorageServiceImpl(
    private val fileRepository: FileRepository,
    private val uploadFileProperties: UploadFileProperties
) : StorageService {

    override fun openAccess(requestDocumentIds: List<DocumentId>): Result<List<OpenAccessResult>, Fail> {

        val documentIds = validationDocumentIds(ids = requestDocumentIds)
            .doOnError {error -> return Result.failure(error) }
            .get

        val dbFiles = getDocumentsByIds(documentIds)
            .doOnError {error -> return Result.failure(error) }
            .get

        if (dbFiles.isEmpty()) {
            return Result.failure(ValidationErrors.DocumentsNotExisting(documentIds))
        }

        val receivedDocumentIds = requestDocumentIds.toSet()
        val knownDocumentIds = dbFiles.toSetBy { it.id }
        val unknownDocumentIds = knownDocumentIds.getUnknownElements(received = receivedDocumentIds)
        if (unknownDocumentIds.isNotEmpty())
            return Result.failure(ValidationErrors.DocumentsNotExisting(unknownDocumentIds))

        return Result.success(
            dbFiles.map { file ->
                OpenAccessResult(
                    id = file.id,
                    datePublished = file.datePublished!!.toLocal(),
                    uri = uploadFileProperties.path + file.id
                )
            }
        )
    }

    override fun checkRegistration(requestDocumentIds: List<DocumentId>): ValidationResult<Fail> {

        val documentIds = validationDocumentIds(ids = requestDocumentIds)
            .doOnError {error -> return ValidationResult.error(error) }
            .get

        val dbFiles = getDocumentsByIds(documentIds)
            .doOnError {error -> return ValidationResult.error(error) }
            .get

        if (dbFiles.isEmpty()) {
            return ValidationResult.error(ValidationErrors.DocumentsNotExisting(documentIds))
        }

        val receivedDocumentIds = requestDocumentIds.toSet()
        val knownDocumentIds = dbFiles.toSetBy { it.id }
        val unknownDocumentIds = knownDocumentIds.getUnknownElements(received = receivedDocumentIds)
        if (unknownDocumentIds.isNotEmpty())
            return ValidationResult.error(ValidationErrors.DocumentsNotExisting(unknownDocumentIds))

        return ValidationResult.ok()
    }

    private fun getDocumentsByIds(documentIds: List<String>) = fileRepository.getAllByIds(documentIds.toSet())

    private fun validationDocumentIds(ids: List<DocumentId>): Result<List<String>, ValidationErrors> =
        ids.mapResult { id -> id validate onBlank }

    private val onBlank =
        ValidationRule { id: DocumentId ->
            if (id.isBlank())
                ValidationResult.error(ValidationErrors.BlankAttribute("id"))
            else
                ValidationResult.ok()
        }
}
