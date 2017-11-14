package com.procurement.storage.repository;

import com.procurement.storage.model.entity.FileUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUrlRepository extends JpaRepository<FileUrlEntity, Long> {

}
