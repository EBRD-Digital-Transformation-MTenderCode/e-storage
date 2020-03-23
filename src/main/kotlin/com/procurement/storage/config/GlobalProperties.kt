package com.procurement.storage.config

import com.procurement.storage.infrastructure.io.orThrow
import com.procurement.storage.infrastructure.web.dto.ApiVersion
import java.util.*

object GlobalProperties {
    val service = Service()

    object App {
        val apiVersion = ApiVersion(major = 1, minor = 0, patch = 0)
    }

    class Service(
        val id: String = "14",
        val name: String = "e-storage",
        val version: String = getGitProperties()
    )

    private fun getGitProperties(): String {
        val gitProps: Properties = try {
            GlobalProperties::class.java.getResourceAsStream("/git.properties")
                .use { stream ->
                    Properties().apply { load(stream) }
                }
        } catch (expected: Exception) {
            throw IllegalStateException(expected)
        }
        return gitProps.orThrow("git.commit.id.abbrev")
    }
}
