package com.procurement.storage.infrastructure.handler.check.registration

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.storage.application.service.StorageService
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.util.Result
import com.procurement.storage.domain.util.ValidationResult
import com.procurement.storage.infrastructure.dto.converter.convert
import com.procurement.storage.infrastructure.handler.AbstractValidationHandler
import com.procurement.storage.model.dto.bpe.Command2Type
import com.procurement.storage.model.dto.bpe.tryGetParams
import org.springframework.stereotype.Service

@Service
class CheckRegistrationHandler(
    private val storageService: StorageService
) : AbstractValidationHandler<Command2Type>() {

    override fun execute(node: JsonNode): ValidationResult<List<Fail>> {

        val params =
            when (val paramsResult = node.tryGetParams(CheckRegistrationRequest::class.java)) {
                is Result.Success -> paramsResult.get
                is Result.Failure -> return ValidationResult.error(listOf(paramsResult.error))
            }
        val data = when (val result = params.convert()) {
            is Result.Success -> result.get
            is Result.Failure -> return ValidationResult.error(result.error)
        }

        val serviceResult = storageService.checkRegistration(requestDocumentIds = data.documentIds)
        if (serviceResult.isError)
            return ValidationResult.error(listOf(serviceResult.error))

        return ValidationResult.ok()
    }

    override val action: Command2Type
        get() = Command2Type.CHECK_REGISTRATION
}
