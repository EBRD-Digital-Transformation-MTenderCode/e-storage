package com.procurement.storage.service;

import com.procurement.storage.model.dto.reservation.ReservationRequestDto;
import com.procurement.storage.model.dto.reservation.ReservationResponseDto;
import org.springframework.stereotype.Service;

@Service
public interface StorageService {

    ReservationResponseDto makeReservation(ReservationRequestDto requestDto);
}
