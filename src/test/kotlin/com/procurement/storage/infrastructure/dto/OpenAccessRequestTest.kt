package com.procurement.storage.infrastructure.dto

import com.procurement.storage.infrastructure.AbstractDTOTestBase
import com.procurement.storage.infrastructure.handler.open.OpenAccessRequest
import org.junit.jupiter.api.Test

class OpenAccessRequestTest : AbstractDTOTestBase<OpenAccessRequest>(
    OpenAccessRequest::class.java){


    @Test
    fun test(){
        testBindingAndMapping(pathToJsonFile = "json/dto/open_access_request_full.json")
    }
}
