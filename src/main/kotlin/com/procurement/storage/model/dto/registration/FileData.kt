package com.procurement.storage.model.dto.registration

import org.springframework.core.io.ByteArrayResource

data class FileData(val fileName: String, val resource: ByteArrayResource)
