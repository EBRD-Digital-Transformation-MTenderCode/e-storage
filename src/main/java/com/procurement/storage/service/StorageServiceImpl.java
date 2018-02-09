package com.procurement.storage.service;

import com.datastax.driver.core.utils.UUIDs;
import com.procurement.storage.exception.GetFileException;
import com.procurement.storage.exception.PublishFileException;
import com.procurement.storage.exception.RegistrationValidationException;
import com.procurement.storage.exception.UploadFileValidationException;
import com.procurement.storage.model.dto.bpe.ResponseDto;
import com.procurement.storage.model.dto.registration.DataDto;
import com.procurement.storage.model.dto.registration.DocumentsDto;
import com.procurement.storage.model.dto.registration.FileDto;
import com.procurement.storage.model.dto.registration.RegistrationRequestDto;
import com.procurement.storage.model.entity.FileEntity;
import com.procurement.storage.repository.FileRepository;
import com.procurement.storage.utils.DateUtil;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import liquibase.util.file.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageServiceImpl implements StorageService {

    private final FileRepository fileRepository;

    private final DateUtil dateUtil;

    @Value("${upload.file.path}")
    private String uploadFilePath;

    @Value("${upload.file.folder}")
    private String uploadFileFolder;

    @Value("${upload.file.extensions}")
    private String[] fileExtensions;

    @Value("${upload.file.max-weight}")
    private Integer maxFileWeight;

    public StorageServiceImpl(final FileRepository fileRepository,
                              final DateUtil dateUtil) {
        this.fileRepository = fileRepository;
        this.dateUtil = dateUtil;
    }

    @Override
    public ResponseDto registerFile(final RegistrationRequestDto dto) {
        checkFileWeight(dto.getWeight());
        checkFileExtension(dto.getFileName());
        final FileEntity fileEntity = fileRepository.save(getEntity(dto));
        return getResponseDto(fileEntity);
    }

    @Override
    public ResponseDto uploadFile(String fileId, MultipartFile file) {
        final Optional<FileEntity> entityOptional = fileRepository.getOneById(UUID.fromString(fileId));
        if (entityOptional.isPresent()) {
            FileEntity fileEntity = entityOptional.get();
            checkFileName(fileEntity, file);
            final byte[] uploadBytes = getFileBytes(file);
            checkFileHash(fileEntity, getFileBytes(file));
            checkFileSize(fileEntity, file);
            final String fileOnServerURL = writeFileToDisk(fileEntity, uploadBytes);
            fileEntity.setFileOnServer(fileOnServerURL);
            fileRepository.save(fileEntity);
            return getUploadResponseDto(fileEntity);
        } else {
            throw new UploadFileValidationException("File not found.");
        }
    }

    @Override
    public ResponseDto setPublishDate(String fileId, LocalDateTime datePublished) {
        publish(fileId, datePublished);
        return new ResponseDto(true, null, "ok");
    }

    @Override
    public ResponseDto setPublishDateBatch(final LocalDateTime datePublished, final DocumentsDto requestDto) {
        for (DocumentsDto.Document document : requestDto.getDocuments()) {
            publish(document.getId(), datePublished);
        }
        return new ResponseDto(true, null, "ok");
    }

    @Override
    public FileDto getFileById(final String fileId) {
        final Optional<FileEntity> entityOptional = fileRepository.getOpenById(UUID.fromString(fileId), true);
        if (entityOptional.isPresent()) {
            FileEntity fileEntity = entityOptional.get();
            return new FileDto(fileEntity.getFileName(), readFileFromDisk(fileEntity.getFileOnServer()));
        } else {
            throw new GetFileException("File not found or closed.");
        }
    }

    public void publish(String fileId, LocalDateTime datePublished) {
        final Optional<FileEntity> entityOptional = fileRepository.getOneById(UUID.fromString(fileId));
        if (entityOptional.isPresent()) {
            FileEntity fileEntity = entityOptional.get();
            fileEntity.setDatePublished(datePublished);
            fileEntity.setIsOpen(true);
            fileRepository.save(fileEntity);
        } else {
            throw new PublishFileException("File not found by id: " + fileId);
        }
    }

    private void checkFileWeight(final long fileWeight) {
        if (maxFileWeight < fileWeight) {
            throw new RegistrationValidationException("Invalid file size for registration.");
        }
    }

    private void checkFileExtension(final String fileName) {
        final String fileExtension = FilenameUtils.getExtension(fileName);
        if (!Arrays.asList(fileExtensions).contains(fileExtension)) {
            throw new RegistrationValidationException("Invalid file extension for registration.");
        }
    }

    private void checkFileHash(final FileEntity fileEntity, final byte[] uploadFileBytes) {
        final String uploadFileHash = DigestUtils.md5DigestAsHex(uploadFileBytes).toUpperCase();
        if (!uploadFileHash.equals(fileEntity.getHash())) {
            throw new UploadFileValidationException("Invalid file hash.");
        }
    }

    private void checkFileName(final FileEntity fileEntity, final MultipartFile file) {
        if (!file.getOriginalFilename().equals(fileEntity.getFileName())) {
            throw new UploadFileValidationException("Invalid file name.");
        }
    }

    private void checkFileSize(final FileEntity fileEntity, final MultipartFile file) {
        final long fileSizeMb = file.getSize() / 1024 / 1024;
        if (fileSizeMb > fileEntity.getWeight()) {
            throw new UploadFileValidationException("Invalid file size.");
        }
    }

    private byte[] getFileBytes(final MultipartFile uploadFileBody) {
        if (uploadFileBody == null || uploadFileBody.isEmpty()) {
            throw new UploadFileValidationException("The file is empty.");
        }
        try {
            return uploadFileBody.getBytes();
        } catch (IOException e) {
            throw new UploadFileValidationException("Error reading file contents.");
        }
    }

    private String writeFileToDisk(final FileEntity fileEntity, final byte[] uploadBytes) {
        try {
            final String fileID = fileEntity.getId().toString();
            final String dir = uploadFileFolder + "/" + fileID.substring(0, 2) + "/" + fileID.substring(2, 4) + "/";
            Files.createDirectories(Paths.get(dir));
            final String url = dir + fileID;
            final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(url)));
            stream.write(uploadBytes);
            stream.close();
            return url;
        } catch (IOException e) {
            throw new UploadFileValidationException(e.getMessage());
        }
    }

    private ByteArrayResource readFileFromDisk(final String fileOnServer) {

        try {
            Path path = Paths.get(fileOnServer);
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
            return resource;
        } catch (IOException e) {
            throw new GetFileException(e.getMessage());
        }
    }

    private FileEntity getEntity(final RegistrationRequestDto dto) {
        final FileEntity fileEntity = new FileEntity();
        fileEntity.setId(UUIDs.timeBased());
        fileEntity.setIsOpen(false);
        fileEntity.setDateModified(dateUtil.getNowUTC());
        fileEntity.setHash(dto.getHash());
        fileEntity.setWeight(dto.getWeight());
        fileEntity.setFileName(dto.getFileName());
        return fileEntity;
    }

    private ResponseDto getResponseDto(final FileEntity entity) {
        final String id = entity.getId().toString();
        final String url = uploadFilePath + id;
        final LocalDateTime dateModified = entity.getDateModified();
        return new ResponseDto(true, null, new DataDto(id, url, dateModified, null));
    }

    private ResponseDto getUploadResponseDto(final FileEntity entity) {
        final String id = entity.getId().toString();
        final String url = uploadFilePath + id;
        return new ResponseDto(true, null, new DataDto(null, url, null, null));
    }
}
