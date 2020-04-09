package com.procurement.storage.infrastructure.handler.open

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.application.service.Logger
import com.procurement.storage.application.service.StorageService
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.util.Result
import com.procurement.storage.infrastructure.dto.OpenAccessResult
import com.procurement.storage.infrastructure.dto.converter.convert
import com.procurement.storage.infrastructure.handler.AbstractQueryHandler
import com.procurement.storage.model.dto.bpe.Command2Type
import com.procurement.storage.model.dto.bpe.tryGetParams
import com.procurement.storage.model.dto.bpe.tryParamsToObject
import org.springframework.stereotype.Service

@Service
class OpenAccessHandler(
    private val storageService: StorageService,
    private val logger:Logger
) : AbstractQueryHandler<Command2Type, List<OpenAccessResult>>(logger = logger) {

    override fun execute(node: JsonNode): Result<List<OpenAccessResult>, Fail> {
        val paramsNode = node.tryGetParams()
            .doOnError { error -> return Result.failure(error) }
            .get

        val params = paramsNode.tryParamsToObject(OpenAccessRequest::class.java)
            .doOnError { error ->
                return Result.failure(error)
            }
            .get
            .convert()
            .doOnError { error -> return Result.failure(error) }
            .get

        return storageService.openAccess(params = params)
    }

    override val action: Command2Type
        get() = Command2Type.OPEN_ACCESS
}
