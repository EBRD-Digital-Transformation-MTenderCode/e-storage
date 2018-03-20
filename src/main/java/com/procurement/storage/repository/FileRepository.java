package com.procurement.storage.repository;

import com.procurement.storage.model.entity.FileEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileRepository extends CassandraRepository<FileEntity, String> {
    @Query(value = "select * from storage_files where file_id=?0 LIMIT 1")
    Optional<FileEntity> getOneById(UUID fileId);
}