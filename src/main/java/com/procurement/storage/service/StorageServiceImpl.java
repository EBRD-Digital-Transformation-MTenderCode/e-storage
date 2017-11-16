package com.procurement.storage.service;

import com.procurement.storage.exception.FileValidationException;
import com.procurement.storage.model.dto.reservation.ReservationRequestDto;
import com.procurement.storage.model.dto.reservation.ReservationResponseDto;
import com.procurement.storage.model.entity.BpTypeEntity;
import com.procurement.storage.model.entity.FileEntity;
import com.procurement.storage.repository.BpTypeRepository;
import com.procurement.storage.repository.FileRepository;
import com.procurement.storage.repository.FileRulesRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import liquibase.util.file.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceImpl implements StorageService {

    private FileRulesRepository fileRulesRepository;
    private BpTypeRepository bpTypeRepository;
    private FileRepository fileRepository;

    @Value("${upload.file.path}")
    private String uploadFilePath;

    public StorageServiceImpl(FileRulesRepository fileRulesRepository,
                              BpTypeRepository bpTypeRepository,
                              FileRepository fileRepository) {
        this.fileRulesRepository = fileRulesRepository;
        this.bpTypeRepository = bpTypeRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    public ReservationResponseDto makeReservation(ReservationRequestDto requestDto) {
        checkFileSize(requestDto);
        reservePlaceForFile(requestDto);
        covertDtoToEntity(requestDto);
        return null;
    }

    private void checkFileSize(final ReservationRequestDto requestDto) {
        String fileExtension = getFileExtension(requestDto.getFile()
                                                          .getFileName());

        final int fileSize = getFileSizeRule(requestDto.getBpTypeId(), fileExtension);
        if (fileSize < requestDto.getFile()
                                 .getFileSize()) throw new FileValidationException("Invalid file size.");
    }

    private String getFileExtension(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }

    private int getFileSizeRule(String BpTypeId, String fileExtension) {
        Integer fileSizeRule = fileRulesRepository.getSize(BpTypeId, fileExtension);
        if (Objects.isNull(fileSizeRule))
            throw new FileValidationException("Found no rules for current file extension.");
        return fileSizeRule;
    }

    private void reservePlaceForFile(ReservationRequestDto requestDto) {
        FileEntity fileEntity = savePlaceForFile(requestDto);
        System.out.println(fileEntity.getId());
    }

    public FileEntity savePlaceForFile(ReservationRequestDto requestDto) {
        FileEntity fileEntity = covertDtoToEntity(requestDto);
        return fileRepository.save(fileEntity);
    }

    private FileEntity covertDtoToEntity(ReservationRequestDto requestDto) {
        BpTypeEntity bpTypeEntity = bpTypeRepository.getFirstById(requestDto.getBpTypeId());
        FileEntity fileEntity = new FileEntity();
        LocalDateTime lastChange = LocalDateTime.now();
        fileEntity.setBpeType(bpTypeEntity);
        fileEntity.setFullFileName(requestDto.getFile().getFileName());
        fileEntity.setFileDesc(requestDto.getFile().getDescription());
        fileEntity.setFileSize(requestDto.getFile().getFileSize());
        fileEntity.setFileMd5Sum(requestDto.getFile().getFileMd5Sum());
        fileEntity.setVisibleAll(requestDto.getFile().getOpen());
        fileEntity.setLastChange(lastChange);
        fileEntity.setFileOnServer(uploadFilePath + lastChange.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return fileEntity;
    }
}
