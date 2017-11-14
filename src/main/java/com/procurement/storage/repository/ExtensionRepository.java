package com.procurement.storage.repository;

import com.procurement.storage.model.entity.ExtensionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtensionRepository extends JpaRepository<ExtensionEntity, Long> {

    ExtensionEntity getFirstByName(String s);
}
