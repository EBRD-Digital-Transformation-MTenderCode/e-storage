package com.procurement.storage.domain.fail.incident

import com.procurement.storage.domain.fail.Fail

sealed class DatabaseIncident(code: String, description: String, expected: Exception) :
    Fail.Incident(code, description, expected) {

    class Database(expected: Exception) : DatabaseIncident(
        code = "01",
        description = "Database incident",
        expected = expected
    )
}
