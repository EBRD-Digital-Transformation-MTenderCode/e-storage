package com.procurement.storage.service;

import com.procurement.storage.model.dto.registration.RegistrationRequestDto;
import com.procurement.storage.model.dto.registration.RegistrationResponseDto;
import com.procurement.storage.model.dto.upload.LoadResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface StorageService {

    RegistrationResponseDto makeRegistration(RegistrationRequestDto requestDto);

    LoadResponseDto uploadFile(long fileId, MultipartFile file);

    byte[] getFileById(long fileId);
}
