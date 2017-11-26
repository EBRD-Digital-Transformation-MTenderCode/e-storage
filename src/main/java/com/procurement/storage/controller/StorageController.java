package com.procurement.storage.controller;

import com.procurement.storage.model.dto.loadreserved.LoadRequestDto;
import com.procurement.storage.model.dto.loadreserved.LoadResponseDto;
import com.procurement.storage.model.dto.reservation.ReservRequestDto;
import com.procurement.storage.model.dto.reservation.ReservResponseDto;
import com.procurement.storage.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/files")
public class StorageController {

    private StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(value = "/reservation", method = RequestMethod.POST)
    public ResponseEntity<ReservResponseDto> makeReservation(@RequestBody ReservRequestDto requestDto) {
        ReservResponseDto responseDto = storageService.makeReservation(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @RequestMapping(value = "/loadreserved", method = RequestMethod.POST)
    public ResponseEntity<LoadResponseDto> loadFile(@RequestBody LoadRequestDto requestDto) throws IOException {
        LoadResponseDto responseDto = storageService.loadFile(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
