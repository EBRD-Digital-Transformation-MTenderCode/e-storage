package com.procurement.storage.infrastructure.handler.open

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.application.service.StorageService
import com.procurement.storage.application.service.dto.OpenAccessParams
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.fail.error.DataErrors
import com.procurement.storage.domain.util.Result
import com.procurement.storage.infrastructure.dto.OpenAccessResult
import com.procurement.storage.infrastructure.dto.converter.convert
import com.procurement.storage.infrastructure.handler.AbstractQueryHandler
import com.procurement.storage.model.dto.bpe.Command2Type
import com.procurement.storage.model.dto.bpe.tryGetParams
import org.springframework.stereotype.Service

@Service
class OpenAccessHandler(
    private val storageService: StorageService
) : AbstractQueryHandler<Command2Type, List<OpenAccessResult>>() {

    override fun execute(node: JsonNode): Result<List<OpenAccessResult>, List<Fail>> {

        val params =
            when (val paramsResult = node.tryGetParams(OpenAccessRequest::class.java)) {
                is Result.Success -> paramsResult.get
                is Result.Failure -> return Result.failure(listOf(paramsResult.error))
            }

        val data: OpenAccessParams = when (val result: Result<OpenAccessParams, List<DataErrors>> = params.convert()) {
            is Result.Success -> result.get
            is Result.Failure -> return result

        }

        val serviceResult = storageService.openAccess(requestDocumentIds = data.documentIds)
        if (serviceResult.isFail)
            return Result.failure(listOf(serviceResult.error))

        return Result.success(serviceResult.get)
    }

    override val action: Command2Type
        get() = Command2Type.OPEN_ACCESS
}
