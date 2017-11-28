package com.procurement.storage.service;

import com.procurement.storage.exception.GetFileException;
import com.procurement.storage.exception.ReservFileValidationException;
import com.procurement.storage.exception.UploadFileValidationException;
import com.procurement.storage.model.dto.reservation.ReservFileDto;
import com.procurement.storage.model.dto.reservation.ReservMessageDto;
import com.procurement.storage.model.dto.reservation.ReservRequestDto;
import com.procurement.storage.model.dto.reservation.ReservResponseDto;
import com.procurement.storage.model.dto.upload.LoadFileDto;
import com.procurement.storage.model.dto.upload.LoadMessageDto;
import com.procurement.storage.model.dto.upload.LoadResponseDto;
import com.procurement.storage.model.entity.BpTypeEntity;
import com.procurement.storage.model.entity.FileEntity;
import com.procurement.storage.repository.BpTypeRepository;
import com.procurement.storage.repository.FileRepository;
import com.procurement.storage.repository.FileRulesRepository;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Objects;
import liquibase.util.file.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageServiceImpl implements StorageService {

    private final FileRulesRepository fileRulesRepository;
    private final BpTypeRepository bpTypeRepository;
    private final FileRepository fileRepository;
    private final String linkPath = "/storage/";
    @Value("${upload.file.path}")
    private String uploadFilePath;

    public StorageServiceImpl(final FileRulesRepository fileRulesRepository,
                              final BpTypeRepository bpTypeRepository,
                              final FileRepository fileRepository) {
        this.fileRulesRepository = fileRulesRepository;
        this.bpTypeRepository = bpTypeRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    public ReservResponseDto makeReservation(final ReservRequestDto requestDto) {
        final ReservFileDto fileDto = requestDto.getFile();
        final int maxFileSize = getFileSize(requestDto.getBpTypeId(), fileDto);
        fileDto.setFileSize(maxFileSize);
        requestDto.setFile(fileDto);
        final ReservResponseDto responseDto = reservePlaceForFile(requestDto);
        return responseDto;
    }

    private int getFileSize(final String bpTypeId, final ReservFileDto fileDto) {
        final String fileExtension = getFileExtension(fileDto.getFileName());
        final int fileSize = getFileSizeRule(bpTypeId, fileExtension);
        if (fileSize < fileDto.getFileSize()) {
            throw new ReservFileValidationException("Invalid file size for reservation.");
        }
        return fileSize;
    }

    private String getFileExtension(final String fileName) {
        return FilenameUtils.getExtension(fileName);
    }

    private int getFileSizeRule(final String bpTypeId, final String fileExtension) {
        final Integer fileSizeRule = fileRulesRepository.getSize(bpTypeId, fileExtension);
        if (Objects.isNull(fileSizeRule)) {
            throw new ReservFileValidationException("Found no rules for current file extension.");
        }
        return fileSizeRule;
    }

    private ReservResponseDto reservePlaceForFile(final ReservRequestDto requestDto) {
        final FileEntity fileEntity = savePlaceForFile(requestDto);
        return covertEntityToReservResponseDto(fileEntity);
    }

    public FileEntity savePlaceForFile(final ReservRequestDto requestDto) {
        final FileEntity fileEntity = covertReservRequestDtoToEntity(requestDto);
        return fileRepository.save(fileEntity);
    }

    private FileEntity covertReservRequestDtoToEntity(final ReservRequestDto requestDto) {
        final BpTypeEntity bpTypeEntity = bpTypeRepository.getFirstById(requestDto.getBpTypeId());
        final ReservFileDto reservFileDto = requestDto.getFile();
        final FileEntity fileEntity = new FileEntity();
        fileEntity.setId(new Date().getTime());
        fileEntity.setBpType(bpTypeEntity);
        fileEntity.setFullName(reservFileDto.getFileName());
        fileEntity.setDescription(reservFileDto.getDescription());
        fileEntity.setSize(reservFileDto.getFileSize());
        fileEntity.setHash(reservFileDto.getFileHash());
        fileEntity.setLink(linkPath + fileEntity.getId());
        fileEntity.setIsOpen(reservFileDto.getOpen());
        return fileEntity;
    }

    private ReservResponseDto covertEntityToReservResponseDto(final FileEntity fileEntity) {
        final ReservFileDto reservFileDto = new ReservFileDto();
        reservFileDto.setId(fileEntity.getId());
        reservFileDto.setFileName(fileEntity.getFullName());
        reservFileDto.setFileHash(fileEntity.getHash());
        reservFileDto.setFileSize(fileEntity.getSize());
        reservFileDto.setFileLink(linkPath + fileEntity.getId());
        reservFileDto.setDescription(fileEntity.getDescription());
        reservFileDto.setOpen(fileEntity.getIsOpen());

        final BpTypeEntity bpTypeEntity = fileEntity.getBpType();
        final ReservMessageDto reservMessageDto = new ReservMessageDto();
        reservMessageDto.setBpTypeId(bpTypeEntity.getId());
        reservMessageDto.setBpTypeName(bpTypeEntity.getName());
        reservMessageDto.setFile(reservFileDto);
        final ReservResponseDto responseDto = new ReservResponseDto(null, null, reservMessageDto);
        return responseDto;
    }

    @Override
    public LoadResponseDto uploadFile(final long fileId, final MultipartFile uploadFileBody) {
        final FileEntity fileEntity = fileRepository.getOne(fileId);
        /**checking the size of the file*/
        checkFileSize(fileEntity, uploadFileBody);
        /**checking the hash of the file*/
        final byte[] uploadBytes = getFileBytes(uploadFileBody);
        checkFileHash(fileEntity, uploadBytes);
        /**write a file to disk*/
        final String fileOnServerURL = writeFileToDisk(fileEntity, uploadBytes);
        fileEntity.setFileOnServer(fileOnServerURL);
        fileRepository.save(fileEntity);
        /**covert and response*/
        return covertEntityToLoadResponseDto(fileEntity);
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

    private void checkFileSize(final FileEntity fileEntity, final MultipartFile uploadFileBody) {
        final long fileSizeMb = uploadFileBody.getSize() / 1024 / 1024;
        if (fileSizeMb > fileEntity.getSize()) {
            throw new UploadFileValidationException("Invalid file size.");
        }
    }

    private void checkFileHash(final FileEntity fileEntity, final byte[] uploadBytes) {
        final String uploadFileHash = DigestUtils.md5DigestAsHex(uploadBytes).toUpperCase();
        if (!uploadFileHash.equals(fileEntity.getHash())) {
            throw new UploadFileValidationException("Invalid file hash.");
        }
    }

    private String writeFileToDisk(final FileEntity fileEntity, final byte[] uploadBytes) {
        try {
            final String url = uploadFilePath + fileEntity.getId();
            final BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(url)));
            stream.write(uploadBytes);
            stream.close();
            return url;
        } catch (IOException e) {
            throw new UploadFileValidationException(e.getMessage());
        }
    }

    private LoadResponseDto covertEntityToLoadResponseDto(final FileEntity fileEntity) {
        final LoadFileDto fileDto = new LoadFileDto();
        fileDto.setId(fileEntity.getId());
        fileDto.setFileName(fileEntity.getFullName());
        fileDto.setFileLink(linkPath + fileEntity.getId());
        final BpTypeEntity bpTypeEntity = fileEntity.getBpType();
        final LoadMessageDto messageDto = new LoadMessageDto();
        messageDto.setBpTypeId(bpTypeEntity.getId());
        messageDto.setBpTypeName(bpTypeEntity.getName());
        messageDto.setFile(fileDto);
        final LoadResponseDto responseDto = new LoadResponseDto(null, null, messageDto);
        return responseDto;
    }

    @Override
    public byte[] getFileById(final long fileId) {
        final FileEntity fileEntity = fileRepository.getOne(fileId);
        return readFileFromDisk(fileEntity.getFileOnServer());
    }

    private byte[] readFileFromDisk(final String fileOnServer) {
        try {
            return Files.readAllBytes(Paths.get(fileOnServer));
        } catch (IOException e) {
            throw new GetFileException(e.getMessage());
        }
    }
}
