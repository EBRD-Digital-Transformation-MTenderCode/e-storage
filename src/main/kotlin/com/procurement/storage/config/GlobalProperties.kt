package com.procurement.storage.config

import com.procurement.storage.infrastructure.web.dto.ApiVersion

object GlobalProperties {
    const val serviceId = "9"
    const val serviceName = "e-storage"

    object App {
        val apiVersion = ApiVersion(major = 1, minor = 0, patch = 0)
    }
}
