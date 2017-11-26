package com.procurement.storage.service;

import com.procurement.storage.exception.FileValidationException;
import com.procurement.storage.model.dto.loadreserved.LoadFileDto;
import com.procurement.storage.model.dto.loadreserved.LoadMessageDto;
import com.procurement.storage.model.dto.loadreserved.LoadRequestDto;
import com.procurement.storage.model.dto.loadreserved.LoadResponseDto;
import com.procurement.storage.model.dto.reservation.ReservFileDto;
import com.procurement.storage.model.dto.reservation.ReservMessageDto;
import com.procurement.storage.model.dto.reservation.ReservRequestDto;
import com.procurement.storage.model.dto.reservation.ReservResponseDto;
import com.procurement.storage.model.entity.BpTypeEntity;
import com.procurement.storage.model.entity.FileEntity;
import com.procurement.storage.repository.BpTypeRepository;
import com.procurement.storage.repository.FileRepository;
import com.procurement.storage.repository.FileRulesRepository;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import liquibase.util.file.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public ReservResponseDto makeReservation(ReservRequestDto requestDto) {
        checkFileSize(requestDto);
        ReservResponseDto responseDto = reservePlaceForFile(requestDto);
        return responseDto;
    }

    private void checkFileSize(final ReservRequestDto requestDto) {
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

    private ReservResponseDto reservePlaceForFile(ReservRequestDto requestDto) {
        FileEntity fileEntity = savePlaceForFile(requestDto);
        return covertEntityToReservResponseDto(fileEntity);
    }

    public FileEntity savePlaceForFile(ReservRequestDto requestDto) {
        FileEntity fileEntity = covertReservRequestDtoToEntity(requestDto);
        return fileRepository.save(fileEntity);
    }

    private FileEntity covertReservRequestDtoToEntity(ReservRequestDto requestDto) {
        BpTypeEntity bpTypeEntity = bpTypeRepository.getFirstById(requestDto.getBpTypeId());
        FileEntity fileEntity = new FileEntity();
        long lastChange = new Date().getTime();
        ReservFileDto reservFileDto = requestDto.getFile();
        fileEntity.setBpeType(bpTypeEntity);
        fileEntity.setFullName(reservFileDto.getFileName());
        fileEntity.setDescription(reservFileDto.getDescription());
        fileEntity.setSize(reservFileDto.getFileSize());
        fileEntity.setMd5Sum(reservFileDto.getFileMd5Sum());
        fileEntity.setLink(reservFileDto.getFileName());
        fileEntity.setIsOpen(reservFileDto.getOpen());
        fileEntity.setLastChange(lastChange);
        return fileEntity;
    }

    private ReservResponseDto covertEntityToReservResponseDto(FileEntity fileEntity) {
        ReservFileDto reservFileDto = new ReservFileDto();
        reservFileDto.setId(fileEntity.getId());
        reservFileDto.setFileName(fileEntity.getFullName());
        reservFileDto.setFileMd5Sum(fileEntity.getMd5Sum());
        reservFileDto.setFileSize(fileEntity.getSize());
        reservFileDto.setFileLink("/storage/"+fileEntity.getMd5Sum());
        reservFileDto.setDescription(fileEntity.getDescription());
        reservFileDto.setOpen(fileEntity.getIsOpen());

        BpTypeEntity bpTypeEntity = fileEntity.getBpeType();
        ReservMessageDto reservMessageDto = new ReservMessageDto();
        reservMessageDto.setBpTypeId(bpTypeEntity.getId());
        reservMessageDto.setBpTypeName(bpTypeEntity.getName());
        reservMessageDto.setFile(reservFileDto);
        ReservResponseDto responseDto = new ReservResponseDto("200", "ok", reservMessageDto);
        return responseDto;
    }


    @Override
    public LoadResponseDto loadFile(LoadRequestDto requestDto) throws IOException {

        FileEntity fileEntity = fileRepository.getOne(requestDto.getFile().getId());

        MultipartFile fileBody = requestDto.getFile().getFileBody();

        if (fileBody != null && !fileBody.isEmpty()) {
            byte[] fileBytes = fileBody.getBytes();
            String url = uploadFilePath + fileEntity.getLastChange();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(url)));
            stream.write(fileBytes);
            stream.close();
            fileEntity.setFileOnServer(url);
            fileRepository.save(fileEntity);
        } else {
            throw new IllegalArgumentException("Can't upload. The file is empty.");
        }
        return covertEntityToLoadResponseDto(fileEntity);
    }

    private LoadResponseDto covertEntityToLoadResponseDto(FileEntity fileEntity) {
        LoadFileDto fileDto = new LoadFileDto();
        fileDto.setId(fileEntity.getId());
        fileDto.setFileName(fileEntity.getFullName());
        fileDto.setFileLink("/storage/"+fileEntity.getMd5Sum());

        BpTypeEntity bpTypeEntity = fileEntity.getBpeType();
        LoadMessageDto messageDto = new LoadMessageDto();
        messageDto.setBpTypeId(bpTypeEntity.getId());
        messageDto.setBpTypeName(bpTypeEntity.getName());
        messageDto.setFile(fileDto);
        LoadResponseDto responseDto = new LoadResponseDto("200", "ok", messageDto);
        return responseDto;
    }

}
