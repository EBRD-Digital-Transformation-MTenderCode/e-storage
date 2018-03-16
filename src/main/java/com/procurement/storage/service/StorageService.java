package com.procurement.storage.service;

import com.procurement.storage.model.dto.bpe.ResponseDto;
import com.procurement.storage.model.dto.registration.DocumentsDto;
import com.procurement.storage.model.dto.registration.FileDto;
import com.procurement.storage.model.dto.registration.RegistrationRequestDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface StorageService {

    ResponseDto registerFile(RegistrationRequestDto requestDto);

    ResponseDto uploadFile(String fileId, MultipartFile file);

    ResponseDto setPublishDateBatch(LocalDateTime datePublished, DocumentsDto requestDto);

    FileDto getFileById(String fileId);
}
