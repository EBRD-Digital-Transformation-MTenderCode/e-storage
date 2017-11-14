package com.procurement.storage.controller;

import com.procurement.storage.model.dto.reservation.ReservationRequestDto;
import com.procurement.storage.model.dto.reservation.ReservationResponseDto;
import com.procurement.storage.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
public class StorageController {

    private StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "/reservation", method = RequestMethod.POST)
    public ResponseEntity<ReservationResponseDto> makeReservation(@RequestBody ReservationRequestDto requestDto) {
        ReservationResponseDto responseDto = storageService.makeReservation(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
