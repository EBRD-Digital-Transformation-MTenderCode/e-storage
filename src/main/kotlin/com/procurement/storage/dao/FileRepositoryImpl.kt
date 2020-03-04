package com.procurement.storage.dao

import com.datastax.driver.core.BoundStatement
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.Row
import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.set
import com.procurement.storage.application.repository.FileRepository
import com.procurement.storage.domain.fail.incident.DatabaseIncident
import com.procurement.storage.domain.util.Result
import com.procurement.storage.domain.util.asSuccess
import com.procurement.storage.model.entity.FileEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class FileRepositoryImpl(private val session: Session) : FileRepository {

    companion object {
        private const val FILES_TABLE = "storage_files"
        private const val ID = "file_id"
        private const val IS_OPEN = "file_is_open"
        private const val MODIFIED = "date_modified"
        private const val PUBLISHED = "date_published"
        private const val HASH = "file_hash"
        private const val WEIGHT = "file_weight"
        private const val NAME = "file_name"
        private const val ON_SERVER = "file_on_server"
        private const val OWNER = "file_owner"
        private const val ONE = 1

        private const val GET_ONE_BY_ID = """
               SELECT *
                 FROM $FILES_TABLE
                WHERE $ID=?
                LIMIT $ONE
            """

        private const val GET_ALL_BY_IDS = """
               SELECT *
                 FROM ocds.$FILES_TABLE
                WHERE $ID IN :values;
            """

        private const val SAVE = """
          INSERT INTO $FILES_TABLE(
          $ID,
          $IS_OPEN,
          $MODIFIED,
          $PUBLISHED,
          $HASH,
          $WEIGHT,
          $NAME,
          $ON_SERVER,
          $OWNER
          )
          VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)
            """
    }

    private val prepareGetOneById = session.prepare(GET_ONE_BY_ID)
    private val prepareGetAllByIds = session.prepare(GET_ALL_BY_IDS)
    private val prepareSave = session.prepare(SAVE)

    override fun getOneById(fileId: String): Result<FileEntity?, DatabaseIncident> {
        val query = prepareGetOneById.bind()
            .apply {
                setString(ID, fileId)
            }

        return load(query)
            .doOnError { error -> return Result.failure(error) }
            .get
            .one()
            ?.convertToFileEntity()
            .asSuccess()
    }

    override fun getAllByIds(fileIds: Set<String>): Result<List<FileEntity>, DatabaseIncident> {
        val query = prepareGetAllByIds.bind()
            .setList("values", fileIds.toList())

        val resultSet = load(query)
            .doOnError { error -> return Result.failure(error) }
            .get

        val entities = ArrayList<FileEntity>()
        if (resultSet.isFullyFetched)
            resultSet.forEach { row ->
                entities.add(row.convertToFileEntity())
            }
        return Result.success(entities)
    }

    override fun save(entity: FileEntity): Result<FileEntity, DatabaseIncident> {
        val insert = prepareSave.bind()
            .apply {
                setString(ID, entity.id)
                set(IS_OPEN, entity.isOpen)
                set(MODIFIED, entity.dateModified)
                set(PUBLISHED, entity.datePublished)
                setString(HASH, entity.hash)
                set(WEIGHT, entity.weight)
                setString(NAME, entity.fileName)
                setString(ON_SERVER, entity.fileOnServer)
                setString(OWNER, entity.owner)
            }
        load(insert).doOnError { error -> return Result.failure(error) }

        return Result.success(entity)
    }

    private fun load(statement: BoundStatement): Result<ResultSet, DatabaseIncident> = try {
        Result.success(session.execute(statement))
    } catch (expected: Exception) {
        Result.failure(DatabaseIncident.Database(expected))
    }

    private fun Row.convertToFileEntity() =
        FileEntity(
            id = this.getString(ID),
            isOpen = this.getBool(IS_OPEN),
            dateModified = this.getTimestamp(MODIFIED),
            datePublished = this.getTimestamp(PUBLISHED),
            hash = this.getString(HASH),
            weight = this.getLong(WEIGHT),
            fileName = this.getString(NAME),
            fileOnServer = this.getString(ON_SERVER),
            owner = this.getString(OWNER)
        )
}
