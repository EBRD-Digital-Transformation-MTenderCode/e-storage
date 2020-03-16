package com.procurement.storage.application.repository


import com.procurement.storage.domain.fail.Fail
import com.procurement.storage.domain.util.Result
import com.procurement.storage.model.entity.FileEntity

interface FileRepository {

    fun getOneById(fileId: String): Result<FileEntity?, Fail.Incident.Database>
    fun getAllByIds(fileIds: Set<String>): Result<List<FileEntity>, Fail.Incident.Database>
    fun save(entity: FileEntity): Result<FileEntity, Fail.Incident.Database>
}