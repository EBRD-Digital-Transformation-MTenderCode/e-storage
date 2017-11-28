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
import liquibase.util.file.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;

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
        ReservFileDto fileDto = requestDto.getFile();
        int maxFileSize = getFileSize(requestDto.getBpTypeId(), fileDto);
        fileDto.setFileSize(maxFileSize);
        requestDto.setFile(fileDto);
        ReservResponseDto responseDto = reservePlaceForFile(requestDto);
        return responseDto;
    }

    private int getFileSize(final String bpTypeId, final ReservFileDto fileDto) {
        String fileExtension = getFileExtension(fileDto.getFileName());
        final int fileSize = getFileSizeRule(bpTypeId, fileExtension);
        if (fileSize < fileDto.getFileSize()) throw new ReservFileValidationException("Invalid file size.");
        return fileSize;
    }

    private String getFileExtension(String fileName) {
        return FilenameUtils.getExtension(fileName);
    }

    private int getFileSizeRule(String BpTypeId, String fileExtension) {
        Integer fileSizeRule = fileRulesRepository.getSize(BpTypeId, fileExtension);
        if (Objects.isNull(fileSizeRule))
            throw new ReservFileValidationException("Found no rules for current file extension.");
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
        ReservFileDto reservFileDto = requestDto.getFile();
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(new Date().getTime());
        fileEntity.setBpeType(bpTypeEntity);
        fileEntity.setFullName(reservFileDto.getFileName());
        fileEntity.setDescription(reservFileDto.getDescription());
        fileEntity.setSize(reservFileDto.getFileSize());
        fileEntity.setMd5Sum(reservFileDto.getFileMd5Sum());
        fileEntity.setLink("/storage/" + fileEntity.getId());
        fileEntity.setIsOpen(reservFileDto.getOpen());
        return fileEntity;
    }

    private ReservResponseDto covertEntityToReservResponseDto(FileEntity fileEntity) {
        ReservFileDto reservFileDto = new ReservFileDto();
        reservFileDto.setId(fileEntity.getId());
        reservFileDto.setFileName(fileEntity.getFullName());
        reservFileDto.setFileMd5Sum(fileEntity.getMd5Sum());
        reservFileDto.setFileSize(fileEntity.getSize());
        reservFileDto.setFileLink("/storage/" + fileEntity.getId());
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
    public LoadResponseDto uploadFile(long fileId, MultipartFile uploadFileBody) {
        FileEntity fileEntity = fileRepository.getOne(fileId);
        byte[] uploadBytes = getFileBytes(uploadFileBody);
        /**checking the size of the file*/
        checkFileSize(fileEntity, uploadFileBody);
        /**checking the hash of the file*/
        checkFileHash(fileEntity, uploadBytes);
        /**write a file to disk*/
        String fileOnServerURL = writeFileToDisk(fileEntity, uploadBytes);
        fileEntity.setFileOnServer(fileOnServerURL);
        fileRepository.save(fileEntity);
        /**covert and response*/
        return covertEntityToLoadResponseDto(fileEntity);
    }

    private byte[] getFileBytes(MultipartFile uploadFileBody) {
        if (uploadFileBody == null || uploadFileBody.isEmpty()) {
            throw new UploadFileValidationException("The file is empty.");
        }
        try {
            return uploadFileBody.getBytes();
        } catch (IOException e) {
            throw new UploadFileValidationException("The file is empty.");
        }
    }

    private void checkFileSize(FileEntity fileEntity, MultipartFile uploadFileBody) {
        long fileSizeMb = uploadFileBody.getSize() / 1024 / 1024;
        if (fileSizeMb > fileEntity.getSize()) throw new UploadFileValidationException("Invalid file size.");
    }

    private void checkFileHash(FileEntity fileEntity, byte[] uploadBytes) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            String uploadMd5Sum = DatatypeConverter.printHexBinary(digest).toUpperCase();
            if (!uploadMd5Sum.equals(fileEntity.getMd5Sum()))
                throw new UploadFileValidationException("Invalid file hash.");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private String writeFileToDisk(FileEntity fileEntity, byte[] uploadBytes) {
        try {
            String url = uploadFilePath + fileEntity.getId();
            BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(url)));
            stream.write(uploadBytes);
            stream.close();
            return url;
        } catch (IOException e) {
            throw new UploadFileValidationException(e.getMessage());
        }
    }

    private LoadResponseDto covertEntityToLoadResponseDto(FileEntity fileEntity) {
        LoadFileDto fileDto = new LoadFileDto();
        fileDto.setId(fileEntity.getId());
        fileDto.setFileName(fileEntity.getFullName());
        fileDto.setFileLink("/storage/" + fileEntity.getId());
        BpTypeEntity bpTypeEntity = fileEntity.getBpeType();
        LoadMessageDto messageDto = new LoadMessageDto();
        messageDto.setBpTypeId(bpTypeEntity.getId());
        messageDto.setBpTypeName(bpTypeEntity.getName());
        messageDto.setFile(fileDto);
        LoadResponseDto responseDto = new LoadResponseDto("200", "ok", messageDto);
        return responseDto;
    }

    @Override
    public byte[] getFileById(long fileId) {
        FileEntity fileEntity = fileRepository.getOne(fileId);
        return readFileFromDisk(fileEntity.getFileOnServer());
    }

    private byte[] readFileFromDisk(String fileOnServer) {
        try {
            return Files.readAllBytes(Paths.get(fileOnServer));
        } catch (IOException e) {
            throw new GetFileException(e.getMessage());
        }
    }

}
