package com.procurement.storage.dao

import com.datastax.driver.core.Session
import com.datastax.driver.core.querybuilder.QueryBuilder.set
import com.procurement.storage.model.entity.FileEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class FileDao(private val session: Session) {

    companion object {
        private const val KEY_SPACE = "ocds"
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

        private const val GET_ONE_BY_ID = """
               SELECT $ID,
                      $IS_OPEN,
                      $MODIFIED,
                      $PUBLISHED,
                      $HASH,
                      $WEIGHT,
                      $NAME,
                      $ON_SERVER,
                      $OWNER
                 FROM $KEY_SPACE.$FILES_TABLE
                WHERE $ID=?
            """

        private const val GET_ALL_BY_IDS = """
               SELECT $ID,
                      $IS_OPEN,
                      $MODIFIED,
                      $PUBLISHED,
                      $HASH,
                      $WEIGHT,
                      $NAME,
                      $ON_SERVER,
                      $OWNER
                 FROM $KEY_SPACE.$FILES_TABLE
                WHERE $ID IN :values;
            """

        private const val SAVE = """
          INSERT INTO $KEY_SPACE.$FILES_TABLE(
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

    fun getOneById(fileId: String): FileEntity? {
        val query = prepareGetOneById.bind()
            .apply {
                setString(ID, fileId)
            }
        val row = session.execute(query).one()
        return if (row != null) FileEntity(
            id = row.getString(ID),
            isOpen = row.getBool(IS_OPEN),
            dateModified = row.getTimestamp(MODIFIED),
            datePublished = row.getTimestamp(PUBLISHED),
            hash = row.getString(HASH),
            weight = row.getLong(WEIGHT),
            fileName = row.getString(NAME),
            fileOnServer = row.getString(ON_SERVER),
            owner = row.getString(OWNER)
        ) else null
    }

    fun getAllByIds(fileIds: Set<String>): List<FileEntity> {
        val query = prepareGetAllByIds.bind()
            .setList("values", fileIds.toList())

        val resultSet = session.execute(query)
        val entities = ArrayList<FileEntity>()
        if (resultSet.isFullyFetched)
            resultSet.forEach { row ->
                entities.add(
                    FileEntity(
                        id = row.getString(ID),
                        isOpen = row.getBool(IS_OPEN),
                        dateModified = row.getTimestamp(MODIFIED),
                        datePublished = row.getTimestamp(PUBLISHED),
                        hash = row.getString(HASH),
                        weight = row.getLong(WEIGHT),
                        fileName = row.getString(NAME),
                        fileOnServer = row.getString(ON_SERVER),
                        owner = row.getString(OWNER)
                    )
                )
            }
        return entities
    }

    fun save(entity: FileEntity): FileEntity {
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
        session.execute(insert)
        return entity
    }
}
