package com.procurement.storage.repository;

import com.procurement.storage.model.entity.BpTypeEntity;
import com.procurement.storage.model.entity.ExtensionEntity;
import com.procurement.storage.model.entity.FileSizeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileSizeRepository extends JpaRepository<FileSizeEntity, Long> {

    FileSizeEntity getFirstByBpTypeAndAndExtension (BpTypeEntity bpTypeEntity, ExtensionEntity ext);
}
