package com.procurement.storage.infrastructure.dto

import com.procurement.storage.infrastructure.AbstractDTOTestBase
import com.procurement.storage.infrastructure.handler.check.registration.CheckRegistrationRequest
import org.junit.jupiter.api.Test

class CheckRegistrationRequestTest : AbstractDTOTestBase<CheckRegistrationRequest>(
    CheckRegistrationRequest::class.java) {

    @Test
    fun test() {
        testBindingAndMapping(pathToJsonFile = "json/dto/check_registration_request_full.json")
    }
}
