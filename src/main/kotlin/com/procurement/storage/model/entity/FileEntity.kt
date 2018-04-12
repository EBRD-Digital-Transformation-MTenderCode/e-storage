package com.procurement.storage.model.entity

import org.springframework.data.cassandra.core.cql.PrimaryKeyType
import org.springframework.data.cassandra.core.mapping.Column
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn
import org.springframework.data.cassandra.core.mapping.Table
import java.util.*

@Table(value = "storage_files")
data class FileEntity(

        @PrimaryKeyColumn(name = "file_id", type = PrimaryKeyType.PARTITIONED)
        var id: UUID,

        @PrimaryKeyColumn(name = "file_is_open", type = PrimaryKeyType.CLUSTERED)
        var isOpen: Boolean,

        @Column(value = "date_modified")
        var dateModified: Date?,

        @Column(value = "date_published")
        var datePublished: Date?,

        @Column(value = "file_hash")
        var hash: String?,

        @Column(value = "file_weight")
        var weight: Long?,

        @Column(value = "file_name")
        var fileName: String?,

        @Column(value = "file_on_server")
        var fileOnServer: String?,

        @Column(value = "file_owner")
        var owner: String?

//    fun getDateModified(): LocalDateTime {
//        return LocalDateTime.ofInstant(dateModified!!.toInstant(), ZoneOffset.UTC)
//    }
//
//    fun setDateModified(dateModified: LocalDateTime) {
//        this.dateModified = Date.from(dateModified.toInstant(ZoneOffset.UTC))
//    }
//
//    fun getDatePublished(): LocalDateTime {
//        return LocalDateTime.ofInstant(datePublished!!.toInstant(), ZoneOffset.UTC)
//    }
//
//    fun setDatePublished(datePublished: LocalDateTime) {
//        this.datePublished = Date.from(datePublished.toInstant(ZoneOffset.UTC))
//    }
)
