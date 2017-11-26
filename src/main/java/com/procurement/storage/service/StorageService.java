package com.procurement.storage.service;

import com.procurement.storage.model.dto.loadreserved.LoadRequestDto;
import com.procurement.storage.model.dto.loadreserved.LoadResponseDto;
import com.procurement.storage.model.dto.reservation.ReservRequestDto;
import com.procurement.storage.model.dto.reservation.ReservResponseDto;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface StorageService {

    ReservResponseDto makeReservation(ReservRequestDto requestDto);

    LoadResponseDto loadFile(LoadRequestDto requestDto) throws IOException;
}
