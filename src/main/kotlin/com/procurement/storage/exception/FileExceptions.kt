package com.procurement.storage.exception

data class GetFileException(override val message: String) : RuntimeException()

data class PublishFileException(override val message: String) : RuntimeException()

data class RegistrationValidationException(override val message: String) : RuntimeException()

data class UploadFileValidationException(override val message: String) : RuntimeException()