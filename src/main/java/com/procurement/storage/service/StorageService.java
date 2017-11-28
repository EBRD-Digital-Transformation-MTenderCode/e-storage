package com.procurement.storage.service;

import com.procurement.storage.model.dto.reservation.ReservRequestDto;
import com.procurement.storage.model.dto.reservation.ReservResponseDto;
import com.procurement.storage.model.dto.upload.LoadResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface StorageService {

    ReservResponseDto makeReservation(ReservRequestDto requestDto);

    LoadResponseDto uploadFile(long fileId, MultipartFile file);

    byte[] getFileById(long fileId);
}