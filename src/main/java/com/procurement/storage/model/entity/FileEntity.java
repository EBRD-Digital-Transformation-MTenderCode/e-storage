package com.procurement.storage.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

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

    @PrimaryKeyColumn(name = "date_modified", type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
    private Date dateModified;

    @Column(value = "date_published")
    private Date datePublished;

    @Column(value = "file_hash")
    private String hash;

    @Column(value = "file_weight")
    private Integer weight;

    @Column(value = "file_name")
    private String fileName;

    @Column(value = "file_on_server")
    private String fileOnServer;

    @Column(value = "file_url")
    private String url;

    @Column(value = "file_desc")
    private String description;

}
