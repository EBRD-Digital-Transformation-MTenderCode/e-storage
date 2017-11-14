package com.procurement.storage.model.entity;

import java.time.LocalDateTime;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "files")
public class FileEntity {

    @Id
    @Column(name = "file_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "last_change", nullable = false)
    private LocalDateTime lastChange;

    @Column(name = "full_file_name", nullable = false)
    private String fullFileName;

    @Column(name = "file_on_server", nullable = false)
    private String fileOnServer;

    @Column(name = "file_desc", nullable = false)
    private String fileDesc;

    @Column(name = "visible_all", nullable = false)
    private Boolean visibleAll;

    @Column(name = "file_size")
    private String fileSize;

    @Column(name = "file_md5sum")
    private String fileMd5sum;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_files_bp_types"))
    private BpTypeEntity bpeType;
}
