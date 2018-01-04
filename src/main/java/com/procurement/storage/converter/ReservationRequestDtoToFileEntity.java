package com.procurement.storage.converter;

import com.procurement.storage.model.dto.registration.RegistrationRequestDto;
import org.springframework.core.convert.converter.Converter;

public class ReservationRequestDtoToFileEntity implements Converter<RegistrationRequestDto, FileEntity> {

    @Override
    public FileEntity convert(final RegistrationRequestDto dataDto) {
//        FileEntity fileEntity = new FileEntity();
//
//        fileEntity.setBpeType(dataDto.getBpTypeId());
//
//        fileEntity.setOcId(dataDto.getOcId());
//        fileEntity.setStartDate(dataDto.getTenderPeriod().getStartDate());
//        fileEntity.setEndDate(dataDto.getTenderPeriod().getEndDate());

        return null;
    }
}
