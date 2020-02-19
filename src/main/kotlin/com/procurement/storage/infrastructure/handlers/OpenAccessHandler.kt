package com.procurement.storage.infrastructure.handlers

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.application.service.StorageService
import com.procurement.storage.infrastructure.dto.OpenAccessRequest
import com.procurement.storage.infrastructure.dto.converter.convert
import com.procurement.storage.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.model.dto.bpe.getParams
import com.procurement.storage.model.dto.bpe.getVersion
import com.procurement.storage.utils.toObject
import org.springframework.stereotype.Service

@Service
class OpenAccessHandler(
    private val storageService: StorageService
) {

    fun handle(request: JsonNode): ApiSuccessResponse {
        val id = request.getId()
        val version = request.getVersion()
        val params = request.getParams()
        val data = toObject(OpenAccessRequest::class.java, params).convert()

        val result = storageService.openAccess(requestDocumentIds = data.documentIds)

        return ApiSuccessResponse(id = id, result = result, version = version)
    }
}
