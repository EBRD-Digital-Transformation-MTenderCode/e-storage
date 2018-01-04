package com.procurement.storage.model.dto.registration;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.io.ByteArrayResource;

@Data
@AllArgsConstructor
public class FileDto {

    private String fileName;

    private ByteArrayResource resource;

}
