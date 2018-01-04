package com.procurement.storage.repository;

import com.procurement.storage.model.entity.FileEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends CassandraRepository<FileEntity, String> {
    @Query(value = "select * from orchestrator_operation where transaction_id=?0 LIMIT 1")
    Optional<FileEntity> getOneById(String transactionId);

    @Query(value = "select * from orchestrator_operation where transaction_id=?0 AND operation_step=?1 LIMIT 1")
    Optional<FileEntity> getOneByStep(String transactionId, Integer step);
}
