package com.procurement.storage.infrastructure.dto

import com.procurement.storage.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class OpenAccessResultTest : AbstractDTOTestBase<OpenAccessResult>(OpenAccessResult::class.java) {

    @Test
    fun test() {
        testBindingAndMapping(pathToJsonFile = "json/dto/open_access_result_full.json")
    }
}
