package com.procurement.storage.service;

import com.procurement.storage.exception.FileValidationException;
import com.procurement.storage.model.dto.reservation.FileDto;
import com.procurement.storage.model.dto.reservation.MessageDto;
import com.procurement.storage.model.dto.reservation.ReservationRequestDto;
import com.procurement.storage.model.dto.reservation.ReservationResponseDto;
import com.procurement.storage.model.entity.BpTypeEntity;
import com.procurement.storage.model.entity.FileEntity;
import com.procurement.storage.repository.BpTypeRepository;
import com.procurement.storage.repository.FileRepository;
import com.procurement.storage.repository.FileRulesRepository;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
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
        ReservationResponseDto responseDto = reservePlaceForFile(requestDto);
        return responseDto;
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

    private ReservationResponseDto reservePlaceForFile(ReservationRequestDto requestDto) {
        FileEntity fileEntity = savePlaceForFile(requestDto);
        return covertEntityToDto(fileEntity);
    }

    public FileEntity savePlaceForFile(ReservationRequestDto requestDto) {
        FileEntity fileEntity = covertDtoToEntity(requestDto);
        return fileRepository.save(fileEntity);
    }

    private FileEntity covertDtoToEntity(ReservationRequestDto requestDto) {
        BpTypeEntity bpTypeEntity = bpTypeRepository.getFirstById(requestDto.getBpTypeId());
        FileEntity fileEntity = new FileEntity();
        LocalDateTime lastChange = LocalDateTime.now();
        FileDto fileDto = requestDto.getFile();
        fileEntity.setBpeType(bpTypeEntity);
        fileEntity.setFullFileName(fileDto.getFileName());
        fileEntity.setFileDesc(fileDto.getDescription());
        fileEntity.setFileSize(fileDto.getFileSize());
        fileEntity.setFileMd5Sum(fileDto.getFileMd5Sum());
        fileEntity.setVisibleAll(fileDto.getOpen());
        fileEntity.setLastChange(lastChange);
        fileEntity.setFileOnServer(uploadFilePath + lastChange.atZone(ZoneId.systemDefault())
                                                              .toInstant()
                                                              .toEpochMilli());
        return fileEntity;
    }

    private ReservationResponseDto covertEntityToDto(FileEntity fileEntity) {
        FileDto fileDto = new FileDto();
        fileDto.setId(fileEntity.getId());
        fileDto.setFileName(fileEntity.getFullFileName());
        fileDto.setFileMd5Sum(fileEntity.getFileMd5Sum());
        fileDto.setFileSize(fileEntity.getFileSize());
        fileDto.setFileLink(fileEntity.getFileOnServer());
        fileDto.setDescription(fileEntity.getFileDesc());
        fileDto.setOpen(fileEntity.getVisibleAll());

        BpTypeEntity bpTypeEntity = fileEntity.getBpeType();
        MessageDto messageDto = new MessageDto();
        messageDto.setBpTypeId(bpTypeEntity.getId());
        messageDto.setBpTypeName(bpTypeEntity.getName());
        messageDto.setFile(fileDto);
        ReservationResponseDto responseDto = new ReservationResponseDto("200", "ok", messageDto);
        return responseDto;
    }
}
