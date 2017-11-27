package com.procurement.storage.controller;

import com.procurement.storage.model.dto.reservation.ReservRequestDto;
import com.procurement.storage.model.dto.reservation.ReservResponseDto;
import com.procurement.storage.model.dto.upload.LoadResponseDto;
import com.procurement.storage.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class StorageController {

    private StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/reservation")
    public ResponseEntity<ReservResponseDto> makeReservation(@RequestBody ReservRequestDto requestDto) {
        ReservResponseDto responseDto = storageService.makeReservation(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/loadreserved", consumes = "multipart/form-data")
    public ResponseEntity<LoadResponseDto> loadFile(@RequestParam(value = "fileId") long fileId,
                                                    @RequestParam(value = "file") MultipartFile file) {
        LoadResponseDto responseDto = storageService.loadFile(fileId, file);
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
