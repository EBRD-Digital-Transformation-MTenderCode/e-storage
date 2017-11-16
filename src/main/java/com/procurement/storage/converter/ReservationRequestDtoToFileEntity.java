package com.procurement.storage.converter;

import com.procurement.storage.model.dto.reservation.ReservationRequestDto;
import com.procurement.storage.model.entity.FileEntity;
import org.springframework.core.convert.converter.Converter;

public class ReservationRequestDtoToFileEntity implements Converter<ReservationRequestDto, FileEntity> {

    @Override
    public FileEntity convert(final ReservationRequestDto dataDto) {
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
