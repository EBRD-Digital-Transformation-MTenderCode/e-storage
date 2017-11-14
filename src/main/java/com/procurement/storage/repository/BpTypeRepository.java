package com.procurement.storage.repository;

import com.procurement.storage.model.entity.BpTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BpTypeRepository extends JpaRepository<BpTypeEntity, Long> {

    BpTypeEntity getFirstById(String s);
}
