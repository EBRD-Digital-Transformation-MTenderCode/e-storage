package com.procurement.storage.domain.model.enums

import com.fasterxml.jackson.annotation.JsonValue

enum class ResponseStatus (@JsonValue val value: String){
    SUCCESS("success"),
    FAIL("fail"),
    INCIDENT("incident")
}
