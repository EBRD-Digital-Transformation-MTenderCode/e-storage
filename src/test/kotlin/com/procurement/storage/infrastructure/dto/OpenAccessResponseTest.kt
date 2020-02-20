package com.procurement.storage.infrastructure.dto

import com.procurement.storage.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class OpenAccessResponseTest : AbstractDTOTestBase<OpenAccessResponse>(OpenAccessResponse::class.java) {

    @Test
    fun test() {
        testBindingAndMapping(pathToJsonFile = "json/dto/open_access_response_full.json")
    }
}
