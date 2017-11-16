package com.procurement.storage.repository;

import com.procurement.storage.model.entity.FileRulesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRulesRepository extends JpaRepository<FileRulesEntity, Long> {

    @Query(value = "SELECT file_size FROM file_rules WHERE bp_type_bp_pk = ?1 AND file_ext = ?2", nativeQuery
        = true)
    Integer getSize(String bpTypeId, String ext);
}
