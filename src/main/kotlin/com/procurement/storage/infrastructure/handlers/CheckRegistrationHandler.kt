package com.procurement.storage.infrastructure.handlers

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.application.service.StorageService
import com.procurement.storage.infrastructure.dto.CheckRegistrationRequest
import com.procurement.storage.infrastructure.dto.converter.convert
import com.procurement.storage.infrastructure.web.dto.ApiSuccessResponse
import com.procurement.storage.model.dto.bpe.getId
import com.procurement.storage.model.dto.bpe.getParams
import com.procurement.storage.model.dto.bpe.getVersion
import com.procurement.storage.utils.toObject
import org.springframework.stereotype.Service

@Service
class CheckRegistrationHandler(
    private val storageService: StorageService
) {

    fun handle(request: JsonNode): ApiSuccessResponse {

        val id = request.getId()
        val version = request.getVersion()
        val params = request.getParams()
        val data = toObject(CheckRegistrationRequest::class.java, params).convert()

        storageService.checkRegistration(requestDocumentIds = data.documentIds)

        return ApiSuccessResponse(
            id = id,
            version = version,
            result = null
        )
    }
}
