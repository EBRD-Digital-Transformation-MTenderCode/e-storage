package com.procurement.storage.service;

import com.procurement.storage.model.dto.registration.FileDto;
import com.procurement.storage.model.dto.registration.RegistrationRequestDto;
import com.procurement.storage.model.dto.registration.ResponseDto;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
public interface StorageService {

    ResponseDto registerFile(RegistrationRequestDto requestDto);

    ResponseDto uploadFile(String fileId, MultipartFile file);

    ResponseDto setPublishDate(String fileId, LocalDateTime datePublished);

    FileDto getFileById(String fileId);
}
