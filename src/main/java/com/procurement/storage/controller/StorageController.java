package com.procurement.storage.controller;

import com.procurement.storage.model.dto.bpe.ResponseDto;
import com.procurement.storage.model.dto.registration.DocumentsDto;
import com.procurement.storage.model.dto.registration.FileDto;
import com.procurement.storage.model.dto.registration.RegistrationRequestDto;
import com.procurement.storage.service.StorageService;
import java.time.LocalDateTime;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class StorageController {

    private final StorageService storageService;

    public StorageController(final StorageService storageService) {
        this.storageService = storageService;
    }

    @RequestMapping(method = RequestMethod.POST, value = "/registration")
    public ResponseEntity<ResponseDto> makeRegistration(@RequestBody final RegistrationRequestDto dto) {
        return new ResponseEntity<>(storageService.registerFile(dto), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload/{fileId}", consumes = "multipart/form-data")
    public ResponseEntity<ResponseDto> uploadFile(@PathVariable(value = "fileId", required = true) final String fileId,
                                                  @RequestParam(value = "file") final MultipartFile file) {
        return new ResponseEntity<>(storageService.uploadFile(fileId, file), HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/publish")
    public ResponseEntity<ResponseDto> setPublishDate(@RequestParam(value = "fileId") final String fileId,
                                                 @RequestParam(value = "datePublished")
                                                 @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime datePublished) {
        return new ResponseEntity<>(storageService.setPublishDate(fileId, datePublished), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/publishBatch")
    public ResponseEntity<ResponseDto> setPublishDateBatch(@RequestParam(value = "datePublished")
                                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime datePublished,
                                                           @RequestBody final DocumentsDto dto) {
        return new ResponseEntity<>(storageService.setPublishDateBatch(datePublished, dto), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get/{fileId}")
    public ResponseEntity<Resource> getFile(@PathVariable(value = "fileId") final String fileId) {
        final FileDto file = storageService.getFileById(fileId);
        final ByteArrayResource resource = file.getResource();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
        headers.setContentDisposition(ContentDisposition.parse("attachment; filename=" + file.getFileName()));
        headers.setContentLength(resource.contentLength());
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
