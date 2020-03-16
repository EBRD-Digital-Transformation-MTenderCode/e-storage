package com.procurement.storage.domain.fail.error

import com.procurement.storage.application.service.Logger
import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.model.document.DocumentId

sealed class ValidationErrors(numberError: String, override val description: String) :
    Fail.Error("VR-") {

    override val code: String = prefix + numberError

    class DocumentsNotExisting(documentsIds: Collection<DocumentId>) : ValidationErrors(
        numberError = "01", description = "Documents '$documentsIds' does not exist"
    )

    class BlankAttribute(attribute: String) : ValidationErrors(
        numberError = "02", description = "Attribute '$attribute' is blank"
    )

    override fun logging(logger: Logger) {
        logger.error(message = message)
    }
}
