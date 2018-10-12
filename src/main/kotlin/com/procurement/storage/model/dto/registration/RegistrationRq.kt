package com.procurement.storage.model.dto.registration

import com.fasterxml.jackson.annotation.JsonCreator
import java.util.*

data class RegistrationRq @JsonCreator constructor(

        val id: UUID?,

        val hash: String,

        val weight: Long,

        val fileName: String
)
