package com.procurement.storage.infrastructure.web.dto

data class ApiVersion(val major: Int, val minor: Int, val patch: Int) {
    companion object {
        fun valueOf(version: String): ApiVersion {
            val elements = version.split(".")
            if (elements.isEmpty() || elements.size > 3)
                throw IllegalArgumentException("Invalid value of the api version ($version).")

            val major: Int = elements[0].toIntOrNull()
                ?: throw IllegalArgumentException("Invalid value of the api version ($version).")

            val minor: Int = if (elements.size >= 2) {
                elements[1].toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid value of the api version ($version).")
            } else
                0

            val patch: Int = if (elements.size == 3) {
                elements[2].toIntOrNull()
                    ?: throw IllegalArgumentException("Invalid value of the api version ($version).")
            } else
                0

            return ApiVersion(major = major, minor = minor, patch = patch)
        }
    }

    override fun toString(): String = "$major.$minor.$patch"
}
