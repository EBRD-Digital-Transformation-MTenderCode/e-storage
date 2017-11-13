package com.procurement.storage.model.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "files_url")
public class FileUrlEntity {

    @Id
    @Column(name = "url_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tmp_url", nullable = false)
    private String tmpUrl;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_url_files"))
    private FileEntity file;
}
