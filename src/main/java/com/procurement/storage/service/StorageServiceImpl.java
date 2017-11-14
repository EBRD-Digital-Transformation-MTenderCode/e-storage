package com.procurement.storage.service;

import com.procurement.storage.model.dto.reservation.ReservationRequestDto;
import com.procurement.storage.model.dto.reservation.ReservationResponseDto;
import com.procurement.storage.model.entity.BpTypeEntity;
import com.procurement.storage.model.entity.ExtensionEntity;
import com.procurement.storage.model.entity.FileSizeEntity;
import com.procurement.storage.repository.BpTypeRepository;
import com.procurement.storage.repository.ExtensionRepository;
import com.procurement.storage.repository.FileRepository;
import com.procurement.storage.repository.FileSizeRepository;
import org.springframework.stereotype.Service;

@Service
public class StorageServiceImpl implements StorageService {

    private BpTypeRepository bpTypeRepository;
    private ExtensionRepository extensionRepository;
    private FileSizeRepository fileSizeRepository;

    public StorageServiceImpl(BpTypeRepository bpTypeRepository,
                              ExtensionRepository extensionRepository,
                              FileSizeRepository fileSizeRepository) {
        this.bpTypeRepository = bpTypeRepository;
        this.extensionRepository = extensionRepository;
        this.fileSizeRepository = fileSizeRepository;
    }

    @Override
    public ReservationResponseDto makeReservation(ReservationRequestDto requestDto) {

        BpTypeEntity bpTypeEntity = bpTypeRepository.getFirstById(requestDto.getBpTypeId());

        String extension = "";
        String fileName = requestDto.getFile().getFileName();
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        ExtensionEntity extensionEntity =  extensionRepository.getFirstByName(extension);

        FileSizeEntity fileSizeEntity = fileSizeRepository.getFirstByBpTypeAndAndExtension(bpTypeEntity, extensionEntity);
        if (fileSizeEntity.getSize()<requestDto.getFile().getFileSize()){
            return null;
        }



        return null;
    }
}
