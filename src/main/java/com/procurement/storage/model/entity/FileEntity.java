package com.procurement.storage.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "files")
public class FileEntity {

    @Id
    @Column(name = "file_pk")
    private Long id;

    @Column(name = "full_file_name", nullable = false)
    private String fullName;

    @Column(name = "file_on_server", nullable = false)
    private String fileOnServer;

    @Column(name = "file_link", nullable = false)
    private String link;

    @Column(name = "file_desc", nullable = false)
    private String description;

    @Column(name = "file_is_open", nullable = false)
    private Boolean isOpen;

    @Column(name = "file_size")
    private Integer size;

    @Column(name = "file_md5sum")
    private String md5Sum;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_files_bp_types"))
    private BpTypeEntity bpType;
}
