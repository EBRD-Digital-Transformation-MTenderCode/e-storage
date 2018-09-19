package com.procurement.storage.model.dto.registration

import com.fasterxml.jackson.annotation.JsonCreator

data class RegistrationRq @JsonCreator constructor(

        val hash: String,

        val weight: Long,

        val fileName: String
)
