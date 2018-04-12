package com.procurement.storage.repository

import com.procurement.storage.model.entity.FileEntity
import org.springframework.data.cassandra.repository.CassandraRepository
import org.springframework.data.cassandra.repository.Query
import org.springframework.stereotype.Repository

import java.util.Optional
import java.util.UUID

@Repository
interface FileRepository : CassandraRepository<FileEntity, String> {
    @Query(value = "select * from storage_files where file_id=?0 LIMIT 1")
    fun getOneById(fileId: UUID): FileEntity?
}