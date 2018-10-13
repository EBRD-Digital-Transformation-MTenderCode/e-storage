package com.procurement.storage.model.dto.registration

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.storage.databinding.JsonDateSerializer
import java.time.LocalDateTime

data class RegistrationRs @JsonCreator constructor(

        val data: RegistrationDataRs
)

data class RegistrationDataRs @JsonCreator constructor(

        val id: String?,

        val url: String?,

        @JsonSerialize(using = JsonDateSerializer::class)
        val dateModified: LocalDateTime?,

        @JsonSerialize(using = JsonDateSerializer::class)
        val datePublished: LocalDateTime?
)