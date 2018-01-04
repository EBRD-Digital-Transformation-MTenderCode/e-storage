package com.procurement.storage.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Table("storage_files")
public class FileEntity {

    @PrimaryKeyColumn(name = "file_id", type = PrimaryKeyType.PARTITIONED)
    private UUID id;

    @PrimaryKeyColumn(name = "file_is_open", type = PrimaryKeyType.CLUSTERED)
    private Boolean isOpen;

    @Column(value = "date_modified")
    private Date dateModified;

    @Column(value = "date_published")
    private Date datePublished;

    @Column(value = "file_hash")
    private String hash;

    @Column(value = "file_weight")
    private Long weight;

    @Column(value = "file_name")
    private String fileName;

    @Column(value = "file_on_server")
    private String fileOnServer;

    @Column(value = "file_owner")
    private String owner;

    public LocalDateTime getDateModified() {
        return LocalDateTime.ofInstant(dateModified.toInstant(), ZoneOffset.UTC);
    }

    public void setDateModified(LocalDateTime dateModified) {
        this.dateModified = Date.from(dateModified.toInstant(ZoneOffset.UTC));
    }

    public LocalDateTime getDatePublished() {
        return LocalDateTime.ofInstant(datePublished.toInstant(), ZoneOffset.UTC);
    }

    public void setDatePublished(LocalDateTime datePublished) {
        this.datePublished = Date.from(datePublished.toInstant(ZoneOffset.UTC));
    }
}
