package com.procurement.storage.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "upload.file")
data class UploadFileProperties(
    var path: String = "",
    var folder: String = "",
    var extensions: List<String> = emptyList(),
    var maxWeight: Int = 0
)
