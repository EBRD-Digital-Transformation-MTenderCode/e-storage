package com.procurement.storage.application.repository

import com.procurement.storage.domain.fail.incident.DatabaseIncident
import com.procurement.storage.domain.util.Result
import com.procurement.storage.model.entity.FileEntity

interface FileRepository {

    fun getOneById(fileId: String): Result<FileEntity?, DatabaseIncident>
    fun getAllByIds(fileIds: Set<String>): Result<List<FileEntity>, DatabaseIncident>
    fun save(entity: FileEntity): Result<FileEntity, DatabaseIncident>
}